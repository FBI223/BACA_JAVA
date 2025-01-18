
param(
    [string[]] $TestClasses
)

$currentDirectory = Get-Location
$classesDirectory = "C:\Users\msztu\Desktop\UJ_ZIMOWY_25\PEGAZ\PJAVA\ZADANIA_JAVA_WINDOWS\8\build\classes\java\main"
Set-Location -Path $classesDirectory

# .\run.ps1 "uj.wmii.pwj.anns.tests.MyBeautifulTestSuite", "uj.wmii.pwj.anns.tests.MyBeautifulTestSuite"
#  "uj.wmii.pwj.anns.tests.MyBeautifulTestSuite"                  to jest argument do przekazania w popwershell
#  "uj.wmii.pwj.anns.tests.MyBeautifulTestSuite" , "uj.wmii.pwj.anns.tests.MyBeautifulTestSuite"        to sa argumenty do przekazania w popwershell

foreach ($class in $TestClasses) {
    java -cp . uj.wmii.pwj.anns.MyTestEngine $class
}
Set-Location -Path $currentDirectory