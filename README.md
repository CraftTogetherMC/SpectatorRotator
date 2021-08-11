# SpectatorRotator
Rotate online players in spectator-mode (default: 15)

https://www.spigotmc.org/resources/spectatorrotator.62770/

**Dev-Builds: https://ci.craft-together.de/**

**Commands:**
 - /spectate [seconds] [-noclip] (Aliases: /spec, /rspec, /rspectate)
 - /spectate reload

**Permissions:**
 - sr.spectate
 - sr.bypass
 
 **Configuration:**
 ```
 # Configuration
DisplayMode: "ACTIONBAR" # Use TITLE, ACTIONBAR or CHAT
DisplayTimeout: 0 # Hide Message after X seconds (0 = disabled). Works only with TITLE or ACTIONBAR!

# Messages:
PermissionDenied: "&cYou don't have permission."
InvalidArguments: "&cYou have to provide an interval between 5 and 300 seconds"
NoPlayerFound: "&cNo Player found"
SpectatingTitle: "&6Spectating player&f: &e%targetPlayer%"
RotatorDisabled: "&cSpectatorRotator disabled"```
