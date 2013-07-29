module XmlConfig where

import Text.XML.HaXml.XmlContent
import Text.XML.HaXml.OneOfN


{-Type decls-}

data Config = Config Package Unit (Maybe Attributes) (Maybe Tags)
                     (Maybe Constructors) (Maybe TagsProperties)
            deriving (Eq,Show)
newtype Package = Package String 		deriving (Eq,Show)
data Unit = Unit Prefixes NameTranslationMethod ModuleGeneration
                 (Maybe RootElement) (Maybe Imports) TagEnumClassName
                 AttEnumClassName ParentClassName
          deriving (Eq,Show)
data NameTranslationMethod = NameTranslationMethodCamelCaseTranslation CamelCaseTranslation
                           | NameTranslationMethodGnuStyleTranslation GnuStyleTranslation
                           | NameTranslationMethodNoTranslation NoTranslation
                           deriving (Eq,Show)
data CamelCaseTranslation = CamelCaseTranslation 		deriving (Eq,Show)
data GnuStyleTranslation = GnuStyleTranslation 		deriving (Eq,Show)
data NoTranslation = NoTranslation 		deriving (Eq,Show)
data ModuleGeneration = ModuleGenerationGenerateStaticModel GenerateStaticModel
                      | ModuleGenerationGenerateDomModel GenerateDomModel
                      deriving (Eq,Show)
data GenerateStaticModel = GenerateStaticModel 		deriving (Eq,Show)
data GenerateDomModel = GenerateDomModel 		deriving (Eq,Show)
newtype RootElement = RootElement String 		deriving (Eq,Show)
data Prefixes = Prefixes
    { prefixesAttPrefix :: (Defaultable String)
    , prefixesTagPrefix :: (Defaultable String)
    , prefixesAttEnumPrefix :: (Defaultable String)
    , prefixesTagEnumPrefix :: (Defaultable String)
    } deriving (Eq,Show)
newtype Imports = Imports [Import] 		deriving (Eq,Show)
newtype Import = Import String 		deriving (Eq,Show)
newtype TagEnumClassName = TagEnumClassName String 		deriving (Eq,Show)
newtype AttEnumClassName = AttEnumClassName String 		deriving (Eq,Show)
newtype ParentClassName = ParentClassName String 		deriving (Eq,Show)
newtype Attributes = Attributes [Attr] 		deriving (Eq,Show)
data Attr = Attr
    { attrName :: String
    , attrAlias :: String
    , attrType :: (Maybe String)
    } deriving (Eq,Show)
newtype Tags = Tags [Tag] 		deriving (Eq,Show)
data Tag = Tag
    { tagName :: String
    , tagAlias :: String
    } deriving (Eq,Show)
newtype Constructors = Constructors [Constructor] 		deriving (Eq,Show)
data Constructor = Constructor Constructor_Attrs ConstructFunction
                               (List1 Argument)
                 deriving (Eq,Show)
data Constructor_Attrs = Constructor_Attrs
    { constructorName :: String
    } deriving (Eq,Show)
newtype ConstructFunction = ConstructFunction String 		deriving (Eq,Show)
newtype Argument = Argument String 		deriving (Eq,Show)
newtype TagsProperties = TagsProperties [Property] 		deriving (Eq,Show)
data Property = Property
    { propertyTagName :: String
    , propertyContainArbitraryAttributes :: (Defaultable String)
    } deriving (Eq,Show)


{-Instance decls-}

instance HTypeable Config where
    toHType x = Defined "config" [] []
instance XmlContent Config where
    toContents (Config a b c d e f) =
        [CElem (Elem "config" [] (toContents a ++ toContents b ++
                                  maybe [] toContents c ++ maybe [] toContents d ++
                                  maybe [] toContents e ++ maybe [] toContents f)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["config"]
        ; interior e $ return (Config) `apply` parseContents
                       `apply` parseContents `apply` optional parseContents
                       `apply` optional parseContents `apply` optional parseContents
                       `apply` optional parseContents
        } `adjustErr` ("in <config>, "++)

instance HTypeable Package where
    toHType x = Defined "package" [] []
instance XmlContent Package where
    toContents (Package a) =
        [CElem (Elem "package" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["package"]
        ; interior e $ return (Package) `apply` (text `onFail` return "")
        } `adjustErr` ("in <package>, "++)

instance HTypeable Unit where
    toHType x = Defined "unit" [] []
instance XmlContent Unit where
    toContents (Unit a b c d e f g h) =
        [CElem (Elem "unit" [] (toContents a ++ toContents b ++
                                toContents c ++ maybe [] toContents d ++ maybe [] toContents e ++
                                toContents f ++ toContents g ++ toContents h)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["unit"]
        ; interior e $ return (Unit) `apply` parseContents
                       `apply` parseContents `apply` parseContents
                       `apply` optional parseContents `apply` optional parseContents
                       `apply` parseContents `apply` parseContents `apply` parseContents
        } `adjustErr` ("in <unit>, "++)

instance HTypeable NameTranslationMethod where
    toHType x = Defined "nameTranslationMethod" [] []
instance XmlContent NameTranslationMethod where
    toContents (NameTranslationMethodCamelCaseTranslation a) =
        [CElem (Elem "nameTranslationMethod" [] (toContents a) ) ()]
    toContents (NameTranslationMethodGnuStyleTranslation a) =
        [CElem (Elem "nameTranslationMethod" [] (toContents a) ) ()]
    toContents (NameTranslationMethodNoTranslation a) =
        [CElem (Elem "nameTranslationMethod" [] (toContents a) ) ()]
    parseContents = do 
        { e@(Elem _ [] _) <- element ["nameTranslationMethod"]
        ; interior e $ oneOf
            [ return (NameTranslationMethodCamelCaseTranslation)
              `apply` parseContents
            , return (NameTranslationMethodGnuStyleTranslation)
              `apply` parseContents
            , return (NameTranslationMethodNoTranslation) `apply` parseContents
            ] `adjustErr` ("in <nameTranslationMethod>, "++)
        }

instance HTypeable CamelCaseTranslation where
    toHType x = Defined "camelCaseTranslation" [] []
instance XmlContent CamelCaseTranslation where
    toContents CamelCaseTranslation =
        [CElem (Elem "camelCaseTranslation" [] []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["camelCaseTranslation"]
        ; return CamelCaseTranslation
        } `adjustErr` ("in <camelCaseTranslation>, "++)

instance HTypeable GnuStyleTranslation where
    toHType x = Defined "gnuStyleTranslation" [] []
instance XmlContent GnuStyleTranslation where
    toContents GnuStyleTranslation =
        [CElem (Elem "gnuStyleTranslation" [] []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["gnuStyleTranslation"]
        ; return GnuStyleTranslation
        } `adjustErr` ("in <gnuStyleTranslation>, "++)

instance HTypeable NoTranslation where
    toHType x = Defined "noTranslation" [] []
instance XmlContent NoTranslation where
    toContents NoTranslation =
        [CElem (Elem "noTranslation" [] []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["noTranslation"]
        ; return NoTranslation
        } `adjustErr` ("in <noTranslation>, "++)

instance HTypeable ModuleGeneration where
    toHType x = Defined "moduleGeneration" [] []
instance XmlContent ModuleGeneration where
    toContents (ModuleGenerationGenerateStaticModel a) =
        [CElem (Elem "moduleGeneration" [] (toContents a) ) ()]
    toContents (ModuleGenerationGenerateDomModel a) =
        [CElem (Elem "moduleGeneration" [] (toContents a) ) ()]
    parseContents = do 
        { e@(Elem _ [] _) <- element ["moduleGeneration"]
        ; interior e $ oneOf
            [ return (ModuleGenerationGenerateStaticModel)
              `apply` parseContents
            , return (ModuleGenerationGenerateDomModel) `apply` parseContents
            ] `adjustErr` ("in <moduleGeneration>, "++)
        }

instance HTypeable GenerateStaticModel where
    toHType x = Defined "generateStaticModel" [] []
instance XmlContent GenerateStaticModel where
    toContents GenerateStaticModel =
        [CElem (Elem "generateStaticModel" [] []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["generateStaticModel"]
        ; return GenerateStaticModel
        } `adjustErr` ("in <generateStaticModel>, "++)

instance HTypeable GenerateDomModel where
    toHType x = Defined "generateDomModel" [] []
instance XmlContent GenerateDomModel where
    toContents GenerateDomModel =
        [CElem (Elem "generateDomModel" [] []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["generateDomModel"]
        ; return GenerateDomModel
        } `adjustErr` ("in <generateDomModel>, "++)

instance HTypeable RootElement where
    toHType x = Defined "rootElement" [] []
instance XmlContent RootElement where
    toContents (RootElement a) =
        [CElem (Elem "rootElement" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["rootElement"]
        ; interior e $ return (RootElement)
                       `apply` (text `onFail` return "")
        } `adjustErr` ("in <rootElement>, "++)

instance HTypeable Prefixes where
    toHType x = Defined "prefixes" [] []
instance XmlContent Prefixes where
    toContents as =
        [CElem (Elem "prefixes" (toAttrs as) []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["prefixes"]
        ; return (fromAttrs as)
        } `adjustErr` ("in <prefixes>, "++)
instance XmlAttributes Prefixes where
    fromAttrs as =
        Prefixes
          { prefixesAttPrefix = defaultA fromAttrToStr "a" "attPrefix" as
          , prefixesTagPrefix = defaultA fromAttrToStr "tag" "tagPrefix" as
          , prefixesAttEnumPrefix = defaultA fromAttrToStr "E" "attEnumPrefix" as
          , prefixesTagEnumPrefix = defaultA fromAttrToStr "E" "tagEnumPrefix" as
          }
    toAttrs v = catMaybes 
        [ defaultToAttr toAttrFrStr "attPrefix" (prefixesAttPrefix v)
        , defaultToAttr toAttrFrStr "tagPrefix" (prefixesTagPrefix v)
        , defaultToAttr toAttrFrStr "attEnumPrefix" (prefixesAttEnumPrefix v)
        , defaultToAttr toAttrFrStr "tagEnumPrefix" (prefixesTagEnumPrefix v)
        ]

instance HTypeable Imports where
    toHType x = Defined "imports" [] []
instance XmlContent Imports where
    toContents (Imports a) =
        [CElem (Elem "imports" [] (concatMap toContents a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["imports"]
        ; interior e $ return (Imports) `apply` many parseContents
        } `adjustErr` ("in <imports>, "++)

instance HTypeable Import where
    toHType x = Defined "import" [] []
instance XmlContent Import where
    toContents (Import a) =
        [CElem (Elem "import" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["import"]
        ; interior e $ return (Import) `apply` (text `onFail` return "")
        } `adjustErr` ("in <import>, "++)

instance HTypeable TagEnumClassName where
    toHType x = Defined "tagEnumClassName" [] []
instance XmlContent TagEnumClassName where
    toContents (TagEnumClassName a) =
        [CElem (Elem "tagEnumClassName" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["tagEnumClassName"]
        ; interior e $ return (TagEnumClassName)
                       `apply` (text `onFail` return "")
        } `adjustErr` ("in <tagEnumClassName>, "++)

instance HTypeable AttEnumClassName where
    toHType x = Defined "attEnumClassName" [] []
instance XmlContent AttEnumClassName where
    toContents (AttEnumClassName a) =
        [CElem (Elem "attEnumClassName" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["attEnumClassName"]
        ; interior e $ return (AttEnumClassName)
                       `apply` (text `onFail` return "")
        } `adjustErr` ("in <attEnumClassName>, "++)

instance HTypeable ParentClassName where
    toHType x = Defined "parentClassName" [] []
instance XmlContent ParentClassName where
    toContents (ParentClassName a) =
        [CElem (Elem "parentClassName" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["parentClassName"]
        ; interior e $ return (ParentClassName)
                       `apply` (text `onFail` return "")
        } `adjustErr` ("in <parentClassName>, "++)

instance HTypeable Attributes where
    toHType x = Defined "attributes" [] []
instance XmlContent Attributes where
    toContents (Attributes a) =
        [CElem (Elem "attributes" [] (concatMap toContents a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["attributes"]
        ; interior e $ return (Attributes) `apply` many parseContents
        } `adjustErr` ("in <attributes>, "++)

instance HTypeable Attr where
    toHType x = Defined "attr" [] []
instance XmlContent Attr where
    toContents as =
        [CElem (Elem "attr" (toAttrs as) []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["attr"]
        ; return (fromAttrs as)
        } `adjustErr` ("in <attr>, "++)
instance XmlAttributes Attr where
    fromAttrs as =
        Attr
          { attrName = definiteA fromAttrToStr "attr" "name" as
          , attrAlias = definiteA fromAttrToStr "attr" "alias" as
          , attrType = possibleA fromAttrToStr "type" as
          }
    toAttrs v = catMaybes 
        [ toAttrFrStr "name" (attrName v)
        , toAttrFrStr "alias" (attrAlias v)
        , maybeToAttr toAttrFrStr "type" (attrType v)
        ]

instance HTypeable Tags where
    toHType x = Defined "tags" [] []
instance XmlContent Tags where
    toContents (Tags a) =
        [CElem (Elem "tags" [] (concatMap toContents a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["tags"]
        ; interior e $ return (Tags) `apply` many parseContents
        } `adjustErr` ("in <tags>, "++)

instance HTypeable Tag where
    toHType x = Defined "tag" [] []
instance XmlContent Tag where
    toContents as =
        [CElem (Elem "tag" (toAttrs as) []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["tag"]
        ; return (fromAttrs as)
        } `adjustErr` ("in <tag>, "++)
instance XmlAttributes Tag where
    fromAttrs as =
        Tag
          { tagName = definiteA fromAttrToStr "tag" "name" as
          , tagAlias = definiteA fromAttrToStr "tag" "alias" as
          }
    toAttrs v = catMaybes 
        [ toAttrFrStr "name" (tagName v)
        , toAttrFrStr "alias" (tagAlias v)
        ]

instance HTypeable Constructors where
    toHType x = Defined "constructors" [] []
instance XmlContent Constructors where
    toContents (Constructors a) =
        [CElem (Elem "constructors" [] (concatMap toContents a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["constructors"]
        ; interior e $ return (Constructors) `apply` many parseContents
        } `adjustErr` ("in <constructors>, "++)

instance HTypeable Constructor where
    toHType x = Defined "constructor" [] []
instance XmlContent Constructor where
    toContents (Constructor as a b) =
        [CElem (Elem "constructor" (toAttrs as) (toContents a ++
                                                 toContents b)) ()]
    parseContents = do
        { e@(Elem _ as _) <- element ["constructor"]
        ; interior e $ return (Constructor (fromAttrs as))
                       `apply` parseContents `apply` parseContents
        } `adjustErr` ("in <constructor>, "++)
instance XmlAttributes Constructor_Attrs where
    fromAttrs as =
        Constructor_Attrs
          { constructorName = definiteA fromAttrToStr "constructor" "name" as
          }
    toAttrs v = catMaybes 
        [ toAttrFrStr "name" (constructorName v)
        ]

instance HTypeable ConstructFunction where
    toHType x = Defined "constructFunction" [] []
instance XmlContent ConstructFunction where
    toContents (ConstructFunction a) =
        [CElem (Elem "constructFunction" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["constructFunction"]
        ; interior e $ return (ConstructFunction)
                       `apply` (text `onFail` return "")
        } `adjustErr` ("in <constructFunction>, "++)

instance HTypeable Argument where
    toHType x = Defined "argument" [] []
instance XmlContent Argument where
    toContents (Argument a) =
        [CElem (Elem "argument" [] (toText a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["argument"]
        ; interior e $ return (Argument) `apply` (text `onFail` return "")
        } `adjustErr` ("in <argument>, "++)

instance HTypeable TagsProperties where
    toHType x = Defined "tagsProperties" [] []
instance XmlContent TagsProperties where
    toContents (TagsProperties a) =
        [CElem (Elem "tagsProperties" [] (concatMap toContents a)) ()]
    parseContents = do
        { e@(Elem _ [] _) <- element ["tagsProperties"]
        ; interior e $ return (TagsProperties) `apply` many parseContents
        } `adjustErr` ("in <tagsProperties>, "++)

instance HTypeable Property where
    toHType x = Defined "property" [] []
instance XmlContent Property where
    toContents as =
        [CElem (Elem "property" (toAttrs as) []) ()]
    parseContents = do
        { (Elem _ as []) <- element ["property"]
        ; return (fromAttrs as)
        } `adjustErr` ("in <property>, "++)
instance XmlAttributes Property where
    fromAttrs as =
        Property
          { propertyTagName = definiteA fromAttrToStr "property" "tagName" as
          , propertyContainArbitraryAttributes = defaultA fromAttrToStr "False" "containArbitraryAttributes" as
          }
    toAttrs v = catMaybes 
        [ toAttrFrStr "tagName" (propertyTagName v)
        , defaultToAttr toAttrFrStr "containArbitraryAttributes" (propertyContainArbitraryAttributes v)
        ]



{-Done-}
