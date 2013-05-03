::echo %~p0
pushd "%~dp0"
::set rootpath=%~p0
::echo %rootpath%
cd %rootpath%

cd..
cd..
cd flapjack

"creatematrix.exe" -map="Flapjack.map" -genotypes="Flapjack.dat" -matrix="Flapjack_matrix.txt"
start excel "Flapjack_matrix.txt"
 exit
 