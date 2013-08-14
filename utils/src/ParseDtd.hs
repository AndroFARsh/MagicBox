
module ParseDtd where

import qualified Text.XML.HaXml.Parse as X
import qualified Text.XML.HaXml as X
import qualified Data.Map as Map
import Data.List
import Data.Maybe (catMaybes,listToMaybe)

data TagType = TagSingle | TagArray | TagHashtable deriving (Eq, Show)

data TagDescr = TagDescr {
    tagName :: String,
    tagType :: TagType,
    isTagEmpty :: Bool} deriving (Eq, Show)

defaultTagDescr name = TagDescr name TagSingle True

data AttDescr = AttDescr {
    attName :: String,
    attDefault :: String,
    isAttId :: Bool,
    attRequired :: Bool,
    attCanNull :: Bool } deriving (Eq, Show)

defaultAttDescr name = AttDescr name "" False False True

data JavaTypeDecl = JavaTypeDecl {
    name :: String,
    childTags :: [TagDescr],
    childAttributes :: [AttDescr],
    hasPCDATA :: Bool
    } deriving (Eq, Show)

data DtdElem = DtdElemTag X.ContentSpec | DtdElemAtt [X.AttDef] | DtdOther
data PackedDtdDef = PackedDtdDef String (Maybe X.ContentSpec) [X.AttDef]

emptyPackedDtdDef = PackedDtdDef "" Nothing []

readDtd dtdFileName = do
    contents <- readFile dtdFileName
    case X.dtdParse' dtdFileName contents of
        Left err -> return $ Left err
        Right Nothing -> return $ Left "No DTD markup found"
        Right (Just (X.DTD _ _ markup)) -> return $ Right markup 

getIdAttributeName = listToMaybe . filter isAttId . childAttributes

flattenAttributes = nub . map attName . concatMap childAttributes

docTypeToJavaType = catMaybes . map convertSingleDef . packDtd
    where packDtd = map (packDtd' emptyPackedDtdDef) . groupBy combineDtd . map toDtdElem
          toDtdElem (X.Element (X.ElementDecl elName csp)) = (elName, DtdElemTag csp)
          toDtdElem (X.AttList (X.AttListDecl atName attdef)) = (atName, DtdElemAtt attdef)
          toDtdElem _ = ("", DtdOther)
          combineDtd (n1, _) (n2, _) = n1 == n2 
          packDtd' pdtd [] = pdtd
          packDtd' pdd@(PackedDtdDef s cont attdef) ((dname, dval):xs) = 
            case dval of
                DtdElemTag csp' -> packDtd' (PackedDtdDef dname (Just csp') attdef) xs
                DtdElemAtt attdef' -> packDtd' (PackedDtdDef dname cont (attdef' ++ attdef)) xs
                DtdOther -> packDtd' pdd xs 
          convertSingleDef (PackedDtdDef "" _ _) = Nothing
          convertSingleDef (PackedDtdDef _ Nothing _) = Nothing
          convertSingleDef (PackedDtdDef tname (Just consp) attrs) = 
            Just $ JavaTypeDecl tname (parseContentSpec [] consp) (map (parseAttDef False) attrs) (hasPCDATA consp)
          parseContentSpec ar X.EMPTY = ar
          parseContentSpec ar X.ANY = ar 
          parseContentSpec ar (X.Mixed X.PCDATA) = ar 
          parseContentSpec ar (X.Mixed (X.PCDATAplus names)) = ar ++ (map defaultTagDescr names)
          parseContentSpec ar (X.ContentSpec (X.TagName n mod)) = (++) ar $ 
            case mod of
                X.None -> [TagDescr n TagSingle False]
                X.Query -> [TagDescr n TagSingle True]
                X.Star -> [TagDescr n TagArray True]
                X.Plus -> [TagDescr n TagArray False]
          parseContentSpec ar (X.ContentSpec (X.Seq cp mod)) = ar ++ (concatMap (parseContentSpec [] . X.ContentSpec) cp)
          parseContentSpec ar (X.ContentSpec (X.Choice cp mod)) = ar ++ (concatMap (parseContentSpec [] . X.ContentSpec) cp)
          -- do not support other types of content (for now)
          hasPCDATA (X.Mixed X.PCDATA) = True
          hasPCDATA (X.Mixed (X.PCDATAplus _)) = True
          hasPCDATA _ = False 
          extractDefault (X.AttValue ((Left s):_)) = s
          extractDefault (X.AttValue _) = ""
          parseAttDef id (X.AttDef n X.StringType defdecl) = 
            case defdecl of
                X.REQUIRED -> AttDescr n "" id True False
                X.IMPLIED -> AttDescr n "" id False True
                X.DefaultTo val _ -> AttDescr n (extractDefault val) id False False
          parseAttDef id (X.AttDef n (X.TokenizedType X.ID) defdecl) = parseAttDef True (X.AttDef n X.StringType defdecl)
          parseAttDef id (X.AttDef n _ defdecl) = parseAttDef id (X.AttDef n X.StringType defdecl)

parseDtd dtdFileName = do
    dtd <- readDtd dtdFileName
    return $ fmap docTypeToJavaType dtd

