
module Main where

import System.Environment (getArgs, getProgName)

import ConfigParser
import qualified ParseDtd as Dtd
import ConfigReader
import Data.List (intersperse)
import SimpleJava
import System.Directory (createDirectoryIfMissing)
import JavaElementGen (createElementModule, createAttributeModule, createTagsModule)

{-
    To run this application type:
    
    DtdToJava.exe <config> <dtd file> <output directory>

-}

main :: IO ()
main = do
    args <- getArgs
    case args of
        (configFileName:dtdFileName:outDir:[]) -> do
            runDtdToJava configFileName dtdFileName outDir
            return ()
        _ -> do
            putStrLn "Wrong command line arguments."
            putStrLn "Usage: "
            progName <- getProgName
            putStrLn $ 
                progName ++ " <config file> <dtd file> <output directory>"
            return ()

runDtdToJava configFileName dtdFileName outDir = do
    config <- parseConfig configFileName
    dtd <- Dtd.parseDtd dtdFileName
    runConversionWith config dtd outDir

runConversionWith (Right conf) (Right dtd) outDir = 
    let javaElementModules = 
            map (runWithConfig conf . createElementModule) dtd
        javaAttributeModule =
            runWithConfig conf $ createAttributeModule dtd (Dtd.flattenAttributes dtd)
        javaTagsModule =
            runWithConfig conf $ createTagsModule dtd (Dtd.flattenAttributes dtd)
        javaModulePath = 
            outDir ++ "/" ++ (concat $ intersperse "/" $ parseJavaQName $ packageName conf)
        writeJava (name, Just jModule) = 
            writeFile (javaModulePath ++ "/" ++ name ++ ".java") (javaModuleToString jModule)
        writeJava (_, Nothing) = return () 
    in do  
        -- print dtd
        createDirectoryIfMissing True javaModulePath
        mapM_ writeJava (javaAttributeModule:javaTagsModule:javaElementModules)

runConversionWith (Left err) _ _ = 
    do  putStrLn "Error parsing config file: "
        putStrLn err
        return ()

runConversionWith _ (Left err) _ = 
    do  putStrLn "Error parsing DTD file: "
        putStrLn err
        return ()

