
class Config{
  def cleanup = true  //this drops everything and rebuilds
  def driverClass = 'org.h2.Driver'
  def connectionString = /jdbc:h2:C:\Users\Bobby\tiger_ny;LOG=0;UNDO_LOG=0/  //;USER=sa;PASSWORD=/
  def tigerDataFiles = /C:\Users\Bobby\Documents\jgeocoder\jgeocoder\src\data/
  def db = null
}