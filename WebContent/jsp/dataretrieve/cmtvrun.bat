::echo %~p0
pushd "%~dp0"
::set rootpath=%~p0
::echo %rootpath%
::cd %rootpath%
cd..
cd..
cd CMTV
cd bin
"cmtv.bat"
exit