
module ConfigParser where

import Text.XML.HaXml.XmlContent (readXml)
import qualified Text.XML.HaXml.XmlContent as X
import qualified XmlConfig as C
import qualified Data.Map as Map
import Data.Maybe (maybe)

type TagMap = Map.Map String C.Tag
type AttMap = Map.Map String C.Attr
type TagProps = Map.Map String Bool

data NameTranslation = CamelCase | GnuStyle | NoTranslation deriving (Eq, Show)

data ModuleGeneration = StaticModel | DomModel deriving (Eq, Show)

data Config = Config 
    { packageName :: String,
      nameTranslation :: NameTranslation, 
      moduleGeneration :: ModuleGeneration,
      unitDefs :: C.Unit,
      tagDefs :: TagMap,
      attDefs :: AttMap, 
      tagProps :: TagProps } deriving (Eq, Show)

readConfig configFileName = do
    contents <- readFile configFileName
    return $ readXml contents

prefix t conf = 
    let (C.Unit prefx _ _ _ _ _ _ _) = (unitDefs conf)
    in fromDefaultable $ t prefx

attributePrefix = prefix C.prefixesAttPrefix
tagPrefix = prefix C.prefixesTagPrefix
attributeEnumPrefix = prefix C.prefixesAttEnumPrefix
tagEnumPrefix = prefix C.prefixesTagEnumPrefix

tagEnumClassName conf = 
    let (C.Unit _ _ _ _ _ (C.TagEnumClassName s) _ _) = unitDefs conf
    in s
attributeEnumClassName conf = 
    let (C.Unit _ _ _ _ _ _ (C.AttEnumClassName s) _) = unitDefs conf
    in s

fromDefaultable (X.Default a) = a
fromDefaultable (X.NonDefault a) = a

tagAlias name def conf = 
    maybe def C.tagAlias $ name `Map.lookup` (tagDefs conf) 

attributeAlias name def conf = 
    maybe def C.attrAlias $ name `Map.lookup` (attDefs conf) 

importsList conf = 
    let (C.Unit _ _ _ _ im _ _ _) = (unitDefs conf)
        importToString (C.Import a) = a
        fromImports (C.Imports a) = a
    in maybe [] (map importToString . fromImports) im

rootElement conf = 
    let (C.Unit _ _ _ re _ _ _ _) = unitDefs conf
        fromRootElement (C.RootElement s) = s
    in fmap fromRootElement re 

xmlConfigToPlain (C.Config pkg unit atts tags _ tagProps) = 
    let (C.Package pkgName) = pkg
        (C.Unit _ nameTrans moduleGen _ _ _ _ _) = unit
        tagsDefMap = maybe Map.empty tagsToMap tags
        attsDefMap = maybe Map.empty attsToMap atts
        tagsPropsMap = maybe Map.empty propsToMap tagProps
        nameTranslation = 
            case nameTrans of
                C.NameTranslationMethodCamelCaseTranslation _ -> CamelCase
                C.NameTranslationMethodGnuStyleTranslation _ -> GnuStyle
                C.NameTranslationMethodNoTranslation _ -> NoTranslation  
        moduleGeneration = 
            case moduleGen of 
                C.ModuleGenerationGenerateStaticModel _ -> StaticModel
                C.ModuleGenerationGenerateDomModel _ -> DomModel
    in Config pkgName nameTranslation moduleGeneration unit tagsDefMap attsDefMap tagsPropsMap 
    where 
        tagsToMap (C.Tags arr) = 
            Map.fromList $ map tagsPairs arr
        tagsPairs tag = (C.tagName tag, tag)
        attsToMap (C.Attributes arr) = 
            Map.fromList $ map attPairs arr
        attPairs att = (C.attrName att, att)
        propsToMap (C.TagsProperties arr) = 
            Map.fromList $ map propsPairs arr
        propsPairs prop = (C.propertyTagName prop, read $ fromDefaultable $ C.propertyContainArbitraryAttributes prop) 

parseConfig configFileName = do
    conf <- readConfig configFileName
    return $ fmap xmlConfigToPlain conf

