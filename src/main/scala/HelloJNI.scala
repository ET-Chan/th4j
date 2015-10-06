/**
 * Created by et on 23/09/15.
 */
class HelloJNI {

  @native def sayHello()
  System.loadLibrary("HelloJNI")

}
object Sample1 extends App{
  new HelloJNI().sayHello()
}