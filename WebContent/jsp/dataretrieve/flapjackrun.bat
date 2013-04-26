::echo %~p0
pushd "%~dp0"
::set rootpath=%~p0
::echo %rootpath%
cd %rootpath%

cd..
cd..
cd flapjack
if exist "Flapjack.txt" (
	"createproject.exe" -map="Flapjack.map" -genotypes="Flapjack.dat" -qtls="Flapjack.txt"  -project="Flapjack.flapjack"
) 
if not exist "Flapjack.txt" (
	"createproject.exe" -map="Flapjack.map" -genotypes="Flapjack.dat" -project="Flapjack.flapjack"
)
"Flapjack.flapjack"
 exit
 