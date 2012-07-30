
class Config{
  def cleanup = true  //this drops everything and rebuilds
  def driverClass = 'org.h2.Driver'
  def connectionString = /jdbc:h2:C:\Users\jliang\Desktop\jgeocoder\tiger\ca;LOG=0;UNDO_LOG=0/
  def tigerDataFiles = /C:\Users\jliang\Desktop\tiger\CA/
  def db = null
}