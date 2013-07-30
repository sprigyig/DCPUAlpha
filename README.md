DCPUAlpha
=========

A 2D DCPU based space AI thingy

The State of Things:
-------------------

Currently you can run demo.Main and you will get a spaceship in the void. This spaceship runs DCPU16 code from a binary and runs it.

You can interrupt the egines (#1-4 are the corners, clockwise starting in the top right. 5 is the one on top, and 6 is the one on bottom.) The value of register a will determine how "on" the engine is.

Interrupting the sensor (on #7) will fill A with your X location (provided it isn't too big.) B with your Y location, C with error codes if either of the previous fail due to size, X with your rotation in degrees, and Y with your rotation rate in degrees/second.


Where I'm going with this:
-------------------------

Well there will obviously be competitive arena matches, but that isn't the first thing I'm going to work on.

I want to create a single player resource progression experience that builds up a (hopefully somewhat reusable) codebase for the player, instead of dropping them straight into competitive AI battles. Initial tasks will be simple and yield low tier resources. As the player progresses they will be able to construct customized ships out of newer and better parts, but the complexity of obtaining the new resources will increase with their tier. 

I'm also toying with the concept of in-station NPCs who can will do research into new parts, provided you can keep them fed and supplied. (My story/justification for the game is something along the lines of: arrive in the Alpha Centauri System in a big colony/station ship, but the autonomous supply ship kept flying further (perhaps someone messed up the timer endianness =p.) It is up to you to use drones to gather enough resources to bring the colony online.)
