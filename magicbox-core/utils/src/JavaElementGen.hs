
module JavaElementGen where

import ConfigReader
import ConfigParser (ModuleGeneration(..))
import SimpleJava
import ParseDtd 
import Language.Java.Syntax as J
import Control.Monad
import Data.List
import Data.Maybe (fromMaybe, fromJust)
import qualified Data.Map as Map

createElementModule :: JavaTypeDecl -> ConfigReader (String, Maybe J.CompilationUnit)
createElementModule typeDecl = do
    modGen <- getModuleGeneration
    createElementModule' modGen typeDecl

createElementModule' DomModel _ = return ("", Nothing) 
createElementModule' StaticModel typeDecl = do
    pkn <- getPackageName   
    tagName <- toTagName (name typeDecl)
    imports <- moduleImports
    return (tagName, Just $ createModule pkn imports [])  


moduleImports = liftM (map importDeclaration) getImportsList


createAttributeModule :: [JavaTypeDecl]->[String] -> ConfigReader (String, Maybe J.CompilationUnit)
createAttributeModule tags attrs = do
    modGen <- getModuleGeneration
    re <- getRootElement
    let tags' = case re of
            Nothing -> tags
            Just n -> (JavaTypeDecl n [] [] False) : tags
    createAttributeModule' modGen tags' attrs

createAttributeModule' StaticModel _ _ = return ("", Nothing)
createAttributeModule' DomModel tags attrs = do
    pkn <- getPackageName
    modName <- getAttributeEnumClassName
    imports <- moduleImports
    attList <- liftM (zip [0..]) $ mapM (toAttributeEnumName) attrs
    attrsConstants <- mapM (toAttributeEnumName) attrs
    let 
        attNamesMap = zip attrs attrsConstants
        arrayStrs = map (J.InitExp . stringExp) attrs
        array = declareMemberFieldArray [J.Public, J.Static, J.Final] stringType "ID_TO_NAME" (Just arrayStrs) 
        hash = declareMemberField [J.Public, J.Static, J.Final] (classType "Hashtable") "NAME_TO_ID" (Just $ instanceExp "Hashtable" [])
        hashPut (strAttr, id) = putToHash "NAME_TO_ID" (stringExp strAttr) (instanceExp "Integer" [variableExp id]) 
        hashInit = staticInitBlock $ map hashPut $ zip attrs $ map snd attList 
        toMemberField (id, name) = declareMemberField [J.Public, J.Static, J.Final] intType name (Just $ intExp id)
        attEnums = (map toMemberField attList) ++  [toMemberField (toInteger $ length attrs, "COUNT")]
        indexForTag tagAttrs = map (J.InitExp . intExp . toInteger . fromMaybe (-1) . flip elemIndex tagAttrs) attrs 
        tagMapArrayData = map (indexForTag . map attName . childAttributes) tags 
        tagMapArray = declareMemberFieldMatrix [J.Public, J.Static, J.Final] intType "ID_TO_INDEX" (Just tagMapArrayData)
        tagIdxList tagAttrs = map (J.InitExp . variableExp . fromJust . flip lookup attNamesMap) tagAttrs 
        tagMapArrayAData = map (tagIdxList . map attName . childAttributes) tags
        tagMapArrayA = declareMemberFieldMatrix [J.Public, J.Static, J.Final] intType "INDEX_TO_ID" (Just tagMapArrayAData)
        classDecl = declareClassType [J.Public] modName ((array:attEnums) ++ [hash,hashInit,tagMapArray,tagMapArrayA])
    return (modName, Just $ createModule pkn imports [classDecl])


createTagsModule :: [JavaTypeDecl] -> [String] -> ConfigReader (String, Maybe J.CompilationUnit) 
createTagsModule tags attrs = do
    modGen <- getModuleGeneration
    re <- getRootElement
    let tags' = case re of
            Nothing -> tags
            Just n -> (JavaTypeDecl n [] [] False) : tags
    createTagsModule' modGen tags' attrs

createTagsModule' StaticModel _ _ = return ("", Nothing)
createTagsModule' DomModel tags attrs = do
    pkn <- getPackageName
    modName <- getTagEnumClassName
    imports <- moduleImports
    tagList <- liftM (zip [0..]) $ mapM (toTagEnumName . name) tags
    let atList = zip [0..] attrs
    att <- getAttributeEnumClassName
    tagProps <- getTagProperties
    let 
        tagNames = map name tags
        arrayStrs = map (J.InitExp . stringExp) tagNames
        array = declareMemberFieldArray [J.Public, J.Static, J.Final] stringType "ID_TO_NAME" (Just arrayStrs) 
        arrayAttCountsData = map (J.InitExp . intExp . toInteger . length . childAttributes) tags
        arrayAttCounts = declareMemberFieldArray [J.Public, J.Static, J.Final] intType "_attCount" (Just arrayAttCountsData)
        arrayPCDATAData = map (J.InitExp . boolExp . hasPCDATA) tags
        arrayPCDATA = declareMemberFieldArray [J.Public, J.Static, J.Final] boolType "_hasPCDATA" (Just arrayPCDATAData)
        arrayArbitraryAttrsData = map (J.InitExp . boolExp . hasArbProps) tagNames
        hasArbProps name = fromMaybe False (Map.lookup name tagProps)
        arrayArbitraryAttrs = declareMemberFieldArray [J.Public, J.Static, J.Final] boolType "_hasArbitraryAttributes" (Just arrayArbitraryAttrsData)
        keyArray = map getIdAttributeName tags 
    let mMap Nothing = return Nothing
        mMap (Just n) = (toAttributeEnumName $ attName n) >>= (return . Just)
    attConstNames <- mapM mMap keyArray
    let
        arrayIds = map (maybe (J.InitExp $ intExp (-1)) (J.InitExp . variableExp . (++) (att ++ "."))) attConstNames 
        idsArray = declareMemberFieldArray [J.Public, J.Static, J.Final] intType "_keys" (Just arrayIds) 
        hash = declareMemberField [J.Public, J.Static, J.Final] (classType "Hashtable") "NAME_TO_ID" (Just $ instanceExp "Hashtable" [])
        hashPut (strAttr, id) = putToHash "NAME_TO_ID" (stringExp strAttr) (instanceExp "Integer" [variableExp id]) 
        hashInit = staticInitBlock $ map hashPut $ zip tagNames $ map snd tagList 
        toMemberField (id, name) = declareMemberField [J.Public, J.Static, J.Final] intType name (Just $ intExp id)
        tagEnums = (map toMemberField tagList) ++ [toMemberField (toInteger $ length tags, "COUNT")]
        classDecl = declareClassType [J.Public] modName ((array:idsArray:arrayAttCounts:arrayPCDATA:arrayArbitraryAttrs:tagEnums) ++ [hash,hashInit])

    return (modName, Just $ createModule pkn imports [classDecl])

