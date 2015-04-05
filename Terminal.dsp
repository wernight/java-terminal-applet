# Microsoft Developer Studio Project File - Name="Terminal" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 5.00
# ** NE PAS MODIFIER **

# TARGTYPE "Java Virtual Machine Java Project" 0x0809

CFG=Terminal - Java Virtual Machine Debug
!MESSAGE Ce fichier Make est incorrect. Pour générer ce projet à l'aide de\
 NMAKE,
!MESSAGE utilisez la commande Exporter des fichiers Make et exécutez
!MESSAGE 
!MESSAGE NMAKE /f "Terminal.mak".
!MESSAGE 
!MESSAGE Vous pouvez indiquer une configuration lors de l'exécution de NMAKE
!MESSAGE en définissant la macro CFG sur la ligne de commande. Par exemple:
!MESSAGE 
!MESSAGE NMAKE /f "Terminal.mak" CFG="Terminal - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Les configurations possibles sont les suivantes:
!MESSAGE 
!MESSAGE "Terminal - Java Virtual Machine Release" (basé sur\
 "Java Virtual Machine Java Project")
!MESSAGE "Terminal - Java Virtual Machine Debug" (basé sur\
 "Java Virtual Machine Java Project")
!MESSAGE 

# Begin Project
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
JAVA=jvc.exe

!IF  "$(CFG)" == "Terminal - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "C:\WBC"
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /O
# ADD JAVA /O

!ELSEIF  "$(CFG)" == "Terminal - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
# ADD BASE JAVA /g
# ADD JAVA /O
# SUBTRACT JAVA /g

!ENDIF 

# Begin Target

# Name "Terminal - Java Virtual Machine Release"
# Name "Terminal - Java Virtual Machine Debug"
# Begin Group "Source Files"

# PROP Default_Filter "java;html"
# Begin Source File

SOURCE=.\Terminal.java
# End Source File
# End Group
# End Target
# End Project
