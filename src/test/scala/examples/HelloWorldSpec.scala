package examples

import org.specs2._
import org.specs2.specification.Snippets

/**
 * This specification shows how to create examples using the "acceptance" style
 */
class HelloWorldSpec extends Specification with ScalaCheck with Snippets { def is = s2"""

 This is a specification to check the 'Hello world' string

 Type some text there

 ${ true and true and true and true }

 ${snippet{
  // type in some code here


  }}



 The 'Hello world' string should
   contain 11 characters                             $e1
   start with 'Hello'                                $e2
   end with 'world'                                  $e3
                                                     """

  def e1 = "Hello world" must have size(11)
  def e2 = "Hello world" must startWith("Hello")
  def e3 = "Hello world" must endWith("world")

}
