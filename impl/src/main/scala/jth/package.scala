

package object jth {
  
  val defaultPrefix = "TH"
  
  val allTypes = Map(
    "Int"   ->("Long","Int"),
    "Float" ->("Double","Float"),
    "Byte"  ->("Long", "Byte"),
    "Char"  ->("Long","Char"),
    "Short" ->("Long","Short"),
    "Long" -> ("Long","Long"),
    "Double" -> ("Double","Double")
  )
 
}