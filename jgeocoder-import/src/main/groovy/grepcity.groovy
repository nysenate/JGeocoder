
def alls = new HashSet()
new File(Thread.currentThread().getContextClassLoader().getResource('all.txt').getFile()).eachLine{
  if(it == null || it == '') return;
  if(!it.contains(/<b>/)) println it
  try {
    def items = it.split('=')
    alls << items[0].split(/<b>/)[1]
    items[1].split(/[|]/).each{item ->
      alls<< item
    }    
} catch (Exception e) {
  e.printStackTrace()
}

}

alls.each{
  println it
}

//println alls.size()