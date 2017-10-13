# kotlinsimple

####BASICS

#####print
~~~~
System.out.print("Hello, World!");           ->       println("hello"")
~~~~
var val 
~~~~
int a = 1                ->        var a:Int = 1  

final int a = 1          ->         val a:Int = 1
~~~~
#####Null I
~~~~
String name = null           ->       var name : String? = null
~~~~
#####Null II
 ~~~~
 if(text != null){                          
   int length = text.length();      ->       var length:Int = text?.length()
 }                         
 ~~~~
#####Strings Template
 ~~~~
    String name = "John";                                               var name = "John"
    String lastName = "Smith";                                  ->      var lastName = "Smith"
    String text = "My name is: " + name + " " + lastName;               var text = "My name is: $name $lastName"
    String otherText = "My name is: " + name.substring(2);              var otherText = "My name is: ${name.substring(2)}"
 ~~~~
 #####Strings Format
  ~~~~
 String text = "First Line\n" +                      var text = """
               "Second Line\n" +                ->            |First Line                                                                           
               "Third Line";                                  |Second Line
                                                              |Third Line
                                                        """.trimMargin()
  ~~~~
 #####三元运算符
 ~~~~
 String text = x > 5 ? "x > 5" : "x <= 5";      ->     val text = if (x > 5) "x > 5" else "x <= 5"
 ~~~~
 ##### switch / when
 
 ~~~~
 int x = 100;
 String result = "";
 switch(x) {
     case 0:
        result = "abc"
        break;
     case 1:
     case 2:
     case 3:    
         result = "abcd"
         break;
     default:
         result = "abcde"    
         break;
 }
   
 ~~~~
 ->
 ~~~~
    var = 100
    var result = when(x) {
        0 -> "abc"
        in 1...10 -> "form 1 to 10"
        11, 12 -> "11 or 12"
        !in 12...15 -> "not form 12 to 15"
        else-> if(isOdd(x)) { "is odd"} else { "other" }
    }
    
    fun isOdd(flag: Int):Boolean {
        return false
    }
 ~~~~
  