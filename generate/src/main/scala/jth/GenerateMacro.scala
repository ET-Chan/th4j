package jth

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.collection.mutable
import ImplMacro._
/**
 * Created by et on 01/10/15, modified from 
 * http://stackoverflow.com/questions/19791686/type-parameters-on-scala-macro-annotations
 */


@compileTimeOnly("enable macro paradise to expand macro annotations")
class GenerateAllTypes extends StaticAnnotation{
  def macroTransform(annottees: Any*):Any = macro generateAllTypesImpl.impl
}

object generateAllTypesImpl{
  
  
  trait generateMode
  case object Native extends generateMode
  
  
  def impl(c:Context)(annottees: c.Expr[Any]*):c.Expr[Any]={
    import c.universe._
    val allTypesOf = generateTypeOf()
//    /*-----------------------------------*/
//    def getClassNameAndAbstractMethods(classDecl: ClassDef){
//      try{
//        val q"trait $className{..$decls}" = classDecl
//        
//        (className)
//        
//      }catch{
//        case _:MatchError => c.abort(c.enclosingPosition, "Annotation is only supported on traits")
//      }
//    }
//    
//    
//    /*-----------------------------------*/
    /*
     * Automatically generate companion object for a given trait
     * Generate all predefined types for the companion object
     * Default behaviour of modifiedCompanions is native mode,
     * that is all abstract methods are linked to native methods
     * 
     * */
    def modifiedCompanion(classDecl:ClassDef, mode:generateMode = Native) = {
      //(a) typecheck on classDecl
      val checkedClassDef = c.typecheck(classDecl)
      val ClassDef(mods, name, tdefs, Template(parents, self, body)) = checkedClassDef
      val companionName = name.toTermName
      
      //iterate all types needed to be generate, first, map all types into typeOfs
      
      val (methodDefs, fieldDefs) = allTypesOf.map{case (real, accReal, prefix)=>{
//        //(b) applied type, for every methods
       
        val tpelist = appliedType(checkedClassDef.symbol, List(real, accReal))
        val factoryName = TermName("get" + real)
        val fieldName = TermName(real+"Instance")
        val (override_methods, native_methods) = body
        .filter { s=>s.isDef & s.symbol.isMethod & s.symbol.isAbstract }
        .map { s => {
          val method = s.symbol.asMethod
          val methodName = method.name
//          println(methodName, tpelist)
          val MethodType(params, retType) = method.typeSignatureIn(tpelist)
          val (valDefParams, callParams) = params.map { s => {
            val vd = internal.valDef(s)
            (vd, vd.name)
          } }.unzip
//          
          val nativeMethodName = TermName(prefix + methodName)
          val native_method = q"""@native def $nativeMethodName(..$valDefParams):$retType"""
          val override_method = q"""override def $methodName(..$valDefParams) = $nativeMethodName(..$callParams)"""
          (override_method, native_method)
        } }.unzip
//        
        val fieldDecl = q"""
            object $fieldName extends $tpelist{
              Native.register("TH");
              ..$native_methods
              ..$override_methods
            }
          """
        (q"""def $factoryName() = $fieldName""",q"""$fieldDecl """)
        
      }}.unzip
      
      q"""
        object $companionName {         
          ..$methodDefs
          ..$fieldDefs
        }
        """
    }  
    
    
    /*-----------------------------------*/
    
    
    annottees.map(_.tree).toList match {
      
      case (classDecl:ClassDef) :: Nil if classDecl.mods.hasFlag(Flag.TRAIT)=> {
        //start to modify the structure
//        val compDeclOpt = Nil
        val compDecl = modifiedCompanion(classDecl)
        c.Expr(q"""
          $classDecl
          $compDecl
          """)
        
      }
      case _ => {c.abort(c.enclosingPosition, "Invalid Annottee, only support traits!")}
    }
    
    
  }
}



//object GenerateMacro {
//  //Generate All types based on the implementation of TH library
//  //This is also to be used by each generic functions, that's why I cannot
//  
//
//
//
////  def generateAllTypes[T](clazz:Class[T]):Map[String, Any] = macro generateAllTypesImpl[T]
//  def generateAllTypesImpl[T:c.WeakTypeTag](c:Context)
//                                           (clazz:c.Expr[Class[T]]):c.Expr[Map[String, Any]]={
//    //This method generates all types availabe in TH
//
//    import c.universe._
//    val gTpeList = weakTypeOf[T]
//
//    val ret = mutable.Stack[Tree]()
//
//    val defaultTypes = TypeMacro
//    .generateAllTypeTags()
//    .asInstanceOf[List[(Type, Type, String)]]
//    for{(r, aR, prefix)<-defaultTypes}{
//      val extendPrefix = defaultPrefix + prefix
//      val tpeList = appliedType(gTpeList, List(r, aR))
//
//      ret.push(q"""(${r.toString + aR.toString}, overrideFunc(classOf[$tpeList], $extendPrefix))""")
//    }
//    
//
////    for{(realName, (accRealName, prefix))<-defaultTypes))}{
////      
////      
////    }
//    
//    c.Expr[Map[String, T]](q"""List(..${ret.toList}).toMap""")
//
//  }
//}



object jsonMacroInstance extends jsonMacro(false)
object jsonStrictMacroInstance extends jsonMacro(true)

/**
 * "@json" macro annotation for case classes
 *
 * This macro annotation automatically creates a JSON serializer for the annotated case class.
 * The companion object will be automatically created if it does not already exist.
 *
 * If the case class has more than one field, the default Play formatter is used.
 * If the case class has only one field, the field is directly serialized. For example, if A
 * is defined as:
 *
 *     case class A(value: Int)
 *
 * then A(4) will be serialized as '4' instead of '{"value": 4}'.
 */
class json extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro jsonMacroInstance.impl
}

/**
 * "@jsonstrict" macro annotation for case classes
 *
 * Same as "@json" annotation, except that it always uses the default Play formatter.
 * For example, if A is defined as:
 *
 *     case class A(value: Int)
 *
 * then A(4) will be serialized as '{"value": 4}'.
 */
class jsonstrict extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro jsonStrictMacroInstance.impl
}

class jsonMacro(isStrict: Boolean) {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def extractClassNameAndFields(classDecl: ClassDef) = {
      try {
        val q"case class $className(..$fields) extends ..$bases { ..$body }" = classDecl
        (className, fields)
      } catch {
        case _: MatchError => c.abort(c.enclosingPosition, "Annotation is only supported on case class")
      }
    }

    def jsonFormatter(className: TypeName, fields: List[ValDef]) = {
      fields.length match {
        case 0 => c.abort(c.enclosingPosition, "Cannot create json formatter for case class with no fields")
        case 1 if !isStrict => {
          // use the serializer for the field
          q"""
            implicit val jsonAnnotationFormat = {
              import play.api.libs.json._
              Format(
                __.read[${fields.head.tpt}].map(s => ${className.toTermName}(s)),
                new Writes[$className] { def writes(o: $className) = Json.toJson(o.${fields.head.name}) }
              )
            }
          """
        }
        case _ => {
          // use Play's macro
          q"implicit val jsonAnnotationFormat = play.api.libs.json.Json.format[$className]"
        }
      }
    }

    def modifiedCompanion(compDeclOpt: Option[ModuleDef], format: ValDef, className: TypeName) = {
      compDeclOpt map { compDecl =>
        // Add the formatter to the existing companion object
        val q"object $obj extends ..$bases { ..$body }" = compDecl
        q"""
          object $obj extends ..$bases {
            ..$body
            $format
          }
        """
      } getOrElse {
        // Create a companion object with the formatter
        q"object ${className.toTermName} { $format }"
      }
    }

    def modifiedDeclaration(classDecl: ClassDef, compDeclOpt: Option[ModuleDef] = None) = {
      val (className, fields) = extractClassNameAndFields(classDecl)
      val format = jsonFormatter(className, fields)
      val compDecl = modifiedCompanion(compDeclOpt, format, className)

      // Return both the class and companion object declarations
      c.Expr(q"""
        $classDecl
        $compDecl
      """)
    }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case (classDecl: ClassDef) :: (compDecl: ModuleDef) :: Nil => modifiedDeclaration(classDecl, Some(compDecl))
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
