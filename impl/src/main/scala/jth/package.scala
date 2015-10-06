

package object jth {
  
  val defaultPrefix = "TH"
  val allTypes = Map(
    "Int"->("Long","Int"),
    "Float"->("Double","Double")
  )
  .map{case (r,(ar, prefix))=>(r,(ar,defaultPrefix + prefix))}
 
}