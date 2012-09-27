Plugin Name: Replicator by redsgreens

URL        : http://dev.bukkit.org/server-mods/replicator/

Description: Use this plugin to distribute items and blocks to your players by 
             creating Replicator chests that always contain the same inventory
             when opened. Books, items with enchantments, and all normal items
             are supported.  

The Basics : Place the items you want to replicate in a chest, then place a
             sign containing the text "[Replicator]" (without the quotes) on
             the ground next to, above, or below the chest. If permissions are
             configured correctly, then the text on the first line of the sign
             will turn blue, and if the sign is placed next to the chest, it
             will attach itself to the chest. Now, every time a player with
             permission opens the chest, they will always see the items you
             placed in the chest prior to placing the sign.
             
             To name a replicator and restrict access permission to it, type
             a word on any of the three remaining lines of the sign. 

Permissions: The following permissions are checked:
             replicator.create: required to create a replicator 
             replicator.access: this grants access to all replicators
             replicator.access.*: this also grants access to all replicators
             replicator.access.<name>: grants access to replicator named "name"

Config File: The following settings are available in the replicator/config.yml
             file:
             
             VerboseStartup: if this is set to "Yes", Replicator will print the
             values of all settings in the config.yml file at load time. The
             default is "No".
             
             ShowErrorsInClient: toggle that determines if error messages are
             displayed in the Minecraft client. Defaults to "Yes".
             
             AllowNonOpAccess: allows non-operator users to access replicators
             if no permissions plugin is in use. Defaults to "No".
             
             SignTag: allows control of the text on the first line of the 
             replicator signs. Defaults to "Replicator". WARNING: If you
             change this value after creating some replicators, the contents 
             of those replicators will be lost.
