## vsp_astroimagej_plugin

### AstroImageJ plugin to query AAVSO Variable Star Plotter database

README file for Vsp_Demo astroimage java plugin to create radec files.<br/>
The current version (WIN10-1.00) is resricted to queries of the Variable Star Plotter database. <br/><br/>
See the User Guide pdf (below) for software setup and user instructions.

**Download files:**
-  vsp_demo-WIN10-X.XX.jar: win 10 compiled ImageJ plugin jar file
- radec.zip: compressed folder containing sample radec txt and DSS fis files
-  User Guide to VSP AstroImageJ Plugin.pdf

Click 'Releases' link on the right side of the repo Code page<br/>
In Releases page, latest release:<br/>
Click radec.zip and User Guide pdf links to download<br/>
Open Assets, click vsp_demo-WIN10-X.XX.jar to download plugin jar file<br/>

**Software Notes**

VSP_DEMO is an ImageJ plugin, ref: https://imagej.net/Developing_Plugins_for_ImageJ_1.x,
developed to support AstroImageJ photometry software. It  is a Java 8 application, developed on Win10 OS using Eclipse IDE. <BR/>
Maven-based build automation, using an edited POM.xlm file supplied with 
Process_Pixels - ImageJ demo software.  <br/>
External libraries:  To handle json data, Vsp_Demo uses the .plugins/jackson jars, included in AIJ installation.<br/>
Refer source code for javadoc and  software comments. <br/>

The user form is configured as  a modal dialog to block user access to the AIJ toolbar. In testing, accessing
AIJ toolbar with the dialog still open could cause a complete system crash, requiring a power cycle to reset. <br/>

Currently, Windows-AIJ is bundled with Java 7.  Refer user guide for (hopefully!) short-term work-arounds,
 either installing system wide jre 8 or simply downloading and running example files without installing the plugin.
 
The software is intended as 'a capability demonstrator' and is currently restricted to querying the VSP
 database (hence the name). <br/>
 The software includes APASS 'dummy' controls & developing APASS queries  on the Vizier server 
 is probably the next development stage.
 
 **Software Build**
 
 Tested on Win10 PC / Eclipse IDE,  expect same procedure to apply on Linux OS.
 
 1. Navigate to the latest GitHub release page https://github.com/richardflee/vsp_astroimagej_plugin/releases
 2. User Assets =>  click *download source code (zip)* to download vsp_astroimagej_plugin-WIN10-1.00.zip
 3.  Copy (uncompressed) folder vsp_astroimagej_plugin-WIN10-1.00 to an Eclipse workspace (example: vsp_workspace)
 4. Open Eclipse and switch to vsp_workspace
 5. Import Maven project: File | Import | Maven | Existing Maven Projects, click Next
 6. Import Maven Project window opens,  click Browse then select Folder vsp_astroimagej_plugin-WIN10-1.00
 7. In Maven Projects / Projects window, confirm pom.xml *vsp_astroimagej_plugin-WIN10-1.00* checked. click Finish
 8. Project vsp_astroimagej_plugin-WIN10-1.00 opens in Package Explorer. Select project in Package Explorer, and press Alt-F5 to update
 9. Open Java class rfl.astroimagej.dev._plugin.Vsp_Demo, then press Ctrl-F11 to confirm runs as Java application
 10. Select Run | Run Configurations and configure as:<br/> 
  Maven Build | Name = Maven package |Base directory = vsp_astroimagej_plugin-WIN10-1.00 | Goals = package | Skip Tests = Yes<br/>
 11. Click Apply, click Run to start Maven build sequence
12. If BUILD SUCCESS (Console) then navigate to target subdirectory and copy  vsp_demo-WIN10-1.00.jar to <br/>
        AIJ/plugins/Radec Plugins folder (see user guide pdf for plugin install).
13.  Finally, open AIJ and confirm plugin dialog opens (AIJ Toolbar | Plugins | Radec Plugin | VSP Demo)
 

 
 
 
 





