
module SimpleJava where

import qualified Language.Java.Syntax as J
import qualified Language.Java.Pretty as J
import Text.PrettyPrint (render)

javaModuleToString :: J.CompilationUnit -> String
javaModuleToString = render . J.pretty 

-- parses Java qualified name into list of 
-- identifiers
parseJavaQName = parse' []
    where parse' xs [] = xs
          parse' xs str = 
            let (name, rest) = break (=='.') str
            in parse' (xs ++ [name]) (drop 1 rest)
        
-- converts the java qualified name to J.Name
javaQNameToName = J.Name . map J.Ident . parseJavaQName 

createModule name imports classes = 
    J.CompilationUnit (Just $ J.PackageDecl $ javaQNameToName name) imports classes

importDeclaration s = J.ImportDecl False (javaQNameToName s) False

declareClassType mod name bodyDecls = 
    J.ClassTypeDecl $ J.ClassDecl mod (J.Ident name) [] Nothing [] (J.ClassBody bodyDecls) 

declareMemberField mod varType varName initExp = J.MemberDecl $ J.FieldDecl mod varType [var]
    where var = J.VarDecl (J.VarId $ J.Ident varName) (fmap J.InitExp initExp)

declareMemberFieldArray mod varType varName initExp = J.MemberDecl $ J.FieldDecl mod (arrayType varType) [var]
    where var = J.VarDecl (J.VarId $ J.Ident varName) (fmap (J.InitArray . J.ArrayInit) initExp)

declareMemberFieldMatrix mod varType varName initExp = 
    J.MemberDecl $ J.FieldDecl mod (arrayType $ arrayType varType) [var]
    where var = J.VarDecl (J.VarId $ J.Ident varName) (fmap (J.InitArray . J.ArrayInit . map (J.InitArray . J.ArrayInit)) initExp) 

primType = J.PrimType
classType = J.RefType . classRefType
classRefType = J.ClassRefType . classType'
classType' n = J.ClassType [(J.Ident n, [])]

staticInitBlock = J.InitDecl True . J.Block

methodCall var name args = J.MethodInv $
    J.TypeMethodCall (javaQNameToName var) [] (J.Ident name) args

putToHash varName expKey expVal = J.BlockStmt $ J.ExpStmt $ 
    methodCall varName "put" [expKey, expVal]

intType = J.PrimType J.IntT
boolType = J.PrimType J.BooleanT

intExp = J.Lit . J.Int
boolExp = J.Lit . J.Boolean
stringExp = J.Lit . J.String
nullExp = J.Lit J.Null
variableExp = J.ExpName . javaQNameToName

instanceExp name args = J.InstanceCreation [] (classType' name) args Nothing

arrayType = J.RefType . J.ArrayType 

stringType = classType "String" 
