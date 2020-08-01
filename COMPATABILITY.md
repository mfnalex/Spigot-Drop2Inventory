1.13+ because:
- Listener:
  - BlockDropItemEvent didnt exist before
- MendingUtils:
  - Turtle-Shell didnt exist before (could be compared as String though)
  - Trident


Limited support for 1.8+ with those disadvantages:
- Beds might not drop correctly
- Blocks breaking by other blocks (e.g. cacti on top of other cacti, torches on a wall, etc) will drop normally
- Custom plugin drops cannot be detected