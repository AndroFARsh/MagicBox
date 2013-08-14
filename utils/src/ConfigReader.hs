
module ConfigReader where

import ConfigParser 
import Control.Monad.Reader
import Control.Monad (liftM)
import Data.Char

type ConfigReader a = Reader Config a

runWithConfig conf f = runReader f conf

conf :: ConfigReader Config
conf = ask

conf' :: (Config -> a) -> ConfigReader a
conf' = asks

getPackageName = conf' packageName 

getNameTranslation = conf' nameTranslation

getModuleGeneration = conf' moduleGeneration

getTagProperties = conf' tagProps

translateToGnu = translate' []
    where translate' res [] = res
          translate' [] (first:rest) = 
            translate' (toLower first:[]) rest
          translate' res (ch:rest) = 
            let ap = if isUpper ch then ['_', toLower ch]
                      else [toLower ch]
            in translate' (res ++ ap) rest

translateToCamelCase = translate' 
    where translate' [] = []
          translate' (first:rest) = toUpper first:rest
            
translateName name = do
    trans <- getNameTranslation
    case trans of
       CamelCase -> return $ translateToCamelCase name
       GnuStyle -> return $ translateToGnu name
       NoTranslation -> return name

getAttributePrefix = conf' attributePrefix 
getTagPrefix = conf' tagPrefix
getAttributeEnumPrefix = conf' attributeEnumPrefix
getTagEnumPrefix = conf' tagEnumPrefix

getTagAlias name def = conf' (tagAlias name def)
getAttributeAlias name def = conf' (attributeAlias name def)

toGenericName prefF aliasF name = do
    translatedName <- liftM2 (++) prefF $ translateName name
    aliasF name translatedName 

toTagName = toGenericName getTagPrefix getTagAlias
toAttributeName = toGenericName getAttributePrefix getAttributeAlias
toTagEnumName = toGenericName getTagEnumPrefix getTagAlias
toAttributeEnumName = toGenericName getAttributeEnumPrefix getAttributeAlias

getImportsList = conf' importsList

getTagEnumClassName = conf' tagEnumClassName
getAttributeEnumClassName = conf' attributeEnumClassName

getRootElement = conf' rootElement
