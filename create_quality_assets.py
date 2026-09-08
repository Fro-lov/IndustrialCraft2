import os
from PIL import Image, ImageDraw

base_dir = r"e:\minecraft\myMods\onter_ic2\src\main\resources"

# 1. GENERATE PERFECT MINECRAFT GUI
def draw_slot(draw, x, y, w=18, h=18):
    # Standard MC slot border
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=(139, 139, 139))
    draw.line([x, y, x + w - 1, y], fill=(55, 55, 55))
    draw.line([x, y, x, y + h - 1], fill=(55, 55, 55))
    draw.line([x + w - 1, y, x + w - 1, y + h - 1], fill=(255, 255, 255))
    draw.line([x, y + h - 1, x + w - 1, y + h - 1], fill=(255, 255, 255))
    draw.point((x + w - 1, y), fill=(198, 198, 198))
    draw.point((x, y + h - 1), fill=(198, 198, 198))
    draw.rectangle([x + 1, y + 1, x + w - 2, y + h - 2], fill=(139, 139, 139))

def create_base_gui():
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Background 176x166
    draw.rectangle([0, 0, 175, 165], fill=(198, 198, 198))
    # Outer 3D borders
    draw.line([0, 0, 175, 0], fill=(255, 255, 255))
    draw.line([0, 0, 0, 165], fill=(255, 255, 255))
    draw.line([175, 0, 175, 165], fill=(85, 85, 85))
    draw.line([0, 165, 175, 165], fill=(85, 85, 85))
    draw.line([1, 1, 174, 1], fill=(219, 219, 219))
    draw.line([1, 1, 1, 164], fill=(219, 219, 219))
    draw.line([174, 1, 174, 164], fill=(115, 115, 115))
    draw.line([1, 164, 174, 164], fill=(115, 115, 115))

    # Player Inventory (9x3 slots at 8, 84)
    for row in range(3):
        for col in range(9):
            draw_slot(draw, 7 + col * 18, 83 + row * 18)

    # Hotbar (9 slots at 8, 142)
    for col in range(9):
        draw_slot(draw, 7 + col * 18, 141)

    return img, draw

# Machine GUI
img_m, draw_m = create_base_gui()
# Machine Slots: Input (55, 34), Output (115, 34), Battery (55, 52)
draw_slot(draw_m, 55, 34)
# Output slot large border
draw_slot(draw_m, 111, 30, 26, 26)
draw_slot(draw_m, 55, 52)
# 4 Upgrade Slots at (151, 7, 25, 43, 61)
for i in range(4):
    draw_slot(draw_m, 151, 7 + i * 18)

# Empty Progress Arrow at (79, 34)
draw_m.rectangle([79, 34, 102, 49], fill=(160, 160, 160), outline=(100, 100, 100))
# Empty Energy Bolt at (56, 38)
draw_m.rectangle([56, 38, 70, 51], fill=(160, 160, 160), outline=(100, 100, 100))

# Sprite sheet on right side (x >= 176)
# Full Progress Arrow (176, 14 to 198, 29)
draw_m.rectangle([176, 14, 199, 29], fill=(80, 220, 80), outline=(40, 150, 40))
# Full Energy Bolt (176, 0 to 189, 13)
draw_m.rectangle([176, 0, 189, 13], fill=(255, 215, 0), outline=(200, 160, 0))

img_m.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "gui", "container", "machine.png"))

# Generator GUI
img_g, draw_g = create_base_gui()
draw_slot(draw_g, 79, 52) # Fuel
draw_slot(draw_g, 79, 16) # Battery
# Energy gauge outline at (104, 36)
draw_g.rectangle([103, 35, 128, 50], fill=(139, 139, 139), outline=(55, 55, 55))
# Full Flame (176, 0)
draw_g.rectangle([176, 0, 189, 13], fill=(255, 120, 0))
# Full Energy Bar (176, 14)
draw_g.rectangle([176, 14, 200, 28], fill=(255, 215, 0))
img_g.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "gui", "container", "generator.png"))

# Storage GUI
img_s, draw_s = create_base_gui()
draw_slot(draw_s, 55, 16) # Charge
draw_slot(draw_s, 55, 52) # Discharge
# Storage Bar Outline (79, 35, w=48, h=16)
draw_s.rectangle([78, 34, 127, 51], fill=(139, 139, 139), outline=(55, 55, 55))
# Full Energy storage (176, 0)
draw_s.rectangle([176, 0, 224, 16], fill=(230, 40, 40), outline=(150, 20, 20))
img_s.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "gui", "container", "storage.png"))

# 2. GENERATE DETAILED PIXEL-ART BLOCK TEXTURES (16x16)
def create_metal_casing():
    img = Image.new("RGBA", (16, 16), (180, 180, 185))
    draw = ImageDraw.Draw(img)
    # Bevel borders
    draw.line([0, 0, 15, 0], fill=(220, 220, 225))
    draw.line([0, 0, 0, 15], fill=(220, 220, 225))
    draw.line([15, 0, 15, 15], fill=(100, 100, 105))
    draw.line([0, 15, 15, 15], fill=(100, 100, 105))
    # Rivets in 4 corners
    for rx, ry in [(2,2), (13,2), (2,13), (13,13)]:
        draw.point((rx, ry), fill=(70, 70, 75))
    return img, draw

# Machine blocks with distinct icons
# Macerator (metal casing with crusher teeth)
img, d = create_metal_casing()
d.rectangle([4, 4, 11, 11], fill=(50, 50, 55), outline=(30, 30, 35))
d.line([5, 6, 10, 6], fill=(200, 200, 200))
d.line([5, 9, 10, 9], fill=(200, 200, 200))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "macerator.png"))

# Electric Furnace
img, d = create_metal_casing()
d.rectangle([3, 4, 12, 11], fill=(40, 40, 40), outline=(20, 20, 20))
d.rectangle([5, 6, 10, 9], fill=(230, 120, 20)) # Orange heating coil
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "electric_furnace.png"))

# Compressor
img, d = create_metal_casing()
d.rectangle([4, 3, 11, 12], fill=(60, 60, 65))
d.line([5, 4, 10, 4], fill=(240, 240, 70))
d.line([5, 11, 10, 11], fill=(240, 240, 70))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "compressor.png"))

# Extractor
img, d = create_metal_casing()
d.rectangle([4, 4, 11, 11], fill=(50, 50, 55))
d.rectangle([6, 5, 9, 10], fill=(220, 160, 30)) # Orange sticky resin drop
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "extractor.png"))

# Metal Former
img, d = create_metal_casing()
d.rectangle([3, 4, 12, 11], fill=(45, 55, 65))
d.ellipse([5, 5, 10, 10], fill=(180, 180, 190), outline=(100, 100, 110)) # Rolling gear
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "metal_former.png"))

# Generator
img, d = create_metal_casing()
d.rectangle([3, 5, 12, 12], fill=(30, 30, 30), outline=(15, 15, 15))
d.line([4, 7, 11, 7], fill=(200, 50, 10))
d.line([4, 9, 11, 9], fill=(240, 150, 20))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "generator.png"))

# Solar Panels (Classic Solar Grid)
def create_solar(color_main, color_sub, color_frame):
    img = Image.new("RGBA", (16, 16), color_frame)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 15, 15], outline=(color_frame[0]-30, color_frame[1]-30, color_frame[2]-30))
    # 4 Cells
    for cx in [2, 9]:
        for cy in [2, 9]:
            d.rectangle([cx, cy, cx + 4, cy + 4], fill=color_main, outline=color_sub)
    return img

create_solar((30, 60, 160), (70, 120, 220), (160, 160, 160)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "solar_panel.png"))
create_solar((20, 100, 200), (90, 180, 255), (190, 160, 80)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "advanced_solar_panel.png"))
create_solar((90, 30, 160), (180, 80, 255), (80, 80, 90)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "hybrid_solar_panel.png"))
create_solar((20, 160, 170), (80, 240, 255), (40, 120, 130)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "ultimate_hybrid_solar_panel.png"))
create_solar((160, 30, 190), (255, 100, 255), (220, 220, 240)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "quantum_solar_panel.png"))

# Storages
# Batbox (wood/copper)
img = Image.new("RGBA", (16, 16), (140, 95, 50))
d = ImageDraw.Draw(img)
d.rectangle([0, 0, 15, 15], outline=(90, 60, 30))
d.rectangle([4, 4, 11, 11], fill=(180, 100, 40), outline=(120, 50, 20))
d.point((7, 7), fill=(255, 215, 0))
d.point((8, 8), fill=(255, 215, 0))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "batbox.png"))

# CESU (bronze)
img, d = create_metal_casing()
d.rectangle([3, 3, 12, 12], fill=(170, 120, 40), outline=(110, 80, 20))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "cesu.png"))

# MFE (blue/iron)
img, d = create_metal_casing()
d.rectangle([3, 3, 12, 12], fill=(40, 70, 140), outline=(20, 40, 90))
d.ellipse([5, 5, 10, 10], fill=(220, 40, 60)) # Red energy crystal in center
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "mfe.png"))

# MFSU (high tech blue/lapis)
img, d = create_metal_casing()
d.rectangle([2, 2, 13, 13], fill=(30, 40, 80), outline=(10, 20, 40))
d.ellipse([5, 5, 10, 10], fill=(30, 120, 240), outline=(100, 200, 255)) # Lapotron blue glow
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "mfsu.png"))

# Cables
def create_cable_tex(color_ins, color_core):
    img = Image.new("RGBA", (16, 16), color_ins)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, 15, 15], outline=(max(0, color_ins[0]-40), max(0, color_ins[1]-40), max(0, color_ins[2]-40)))
    d.rectangle([5, 5, 10, 10], fill=color_core)
    return img

create_cable_tex((50, 50, 50), (190, 100, 40)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "copper_cable.png"))
create_cable_tex((210, 210, 210), (230, 180, 30)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "gold_cable.png"))
create_cable_tex((30, 30, 30), (220, 220, 230)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "hv_cable.png"))
create_cable_tex((210, 240, 255), (100, 220, 255)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "glass_fibre_cable.png"))
create_cable_tex((120, 40, 200), (230, 100, 255)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", "superconductor_cable.png"))

# 3. GENERATE PIXEL ART ITEMS
def draw_item_base(color_fill, color_outline):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    return img, d

# Electronic Circuit (Green board with gold pins)
img, d = draw_item_base(None, None)
d.rectangle([2, 2, 13, 13], fill=(30, 120, 40), outline=(15, 70, 20))
d.rectangle([5, 5, 10, 10], fill=(20, 20, 20))
d.line([3, 7, 5, 7], fill=(230, 190, 40))
d.line([10, 7, 12, 7], fill=(230, 190, 40))
d.line([7, 3, 7, 5], fill=(230, 190, 40))
d.line([7, 10, 7, 12], fill=(230, 190, 40))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "electronic_circuit.png"))

# Advanced Circuit (Red board with lapis/glowstone chip)
img, d = draw_item_base(None, None)
d.rectangle([2, 2, 13, 13], fill=(160, 30, 30), outline=(90, 15, 15))
d.rectangle([5, 5, 10, 10], fill=(30, 60, 150))
d.point((7, 7), fill=(255, 230, 70))
d.point((8, 8), fill=(255, 230, 70))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "advanced_circuit.png"))

# RE Battery
img, d = draw_item_base(None, None)
d.rectangle([4, 4, 11, 13], fill=(180, 40, 40), outline=(100, 20, 20))
d.rectangle([6, 2, 9, 3], fill=(190, 190, 200), outline=(100, 100, 110))
d.line([5, 8, 10, 8], fill=(255, 215, 0))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "re_battery.png"))

# Energy Crystal (Red diamond shape)
img, d = draw_item_base(None, None)
d.polygon([(7, 1), (13, 7), (7, 14), (1, 7)], fill=(220, 30, 50), outline=(130, 10, 25))
d.polygon([(7, 3), (11, 7), (7, 12), (3, 7)], fill=(255, 80, 100))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "energy_crystal.png"))

# Lapotron Crystal (Blue diamond shape)
img, d = draw_item_base(None, None)
d.polygon([(7, 1), (13, 7), (7, 14), (1, 7)], fill=(30, 80, 230), outline=(15, 40, 130))
d.polygon([(7, 3), (11, 7), (7, 12), (3, 7)], fill=(90, 160, 255))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "lapotron_crystal.png"))

# Rubber & Sticky Resin
img, d = draw_item_base(None, None)
d.ellipse([3, 5, 12, 11], fill=(30, 30, 35), outline=(15, 15, 20))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "rubber.png"))

img, d = draw_item_base(None, None)
d.ellipse([3, 4, 12, 12], fill=(230, 160, 30), outline=(150, 100, 10))
d.point((6, 6), fill=(255, 220, 100))
img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "sticky_resin.png"))

# Plates and Alloys
def draw_plate(color, border):
    img, d = draw_item_base(None, None)
    d.rectangle([2, 3, 13, 12], fill=color, outline=border)
    return img

draw_plate((160, 140, 130), (100, 90, 80)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "advanced_alloy.png"))
draw_plate((150, 150, 160), (90, 90, 100)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "mixed_metal_ingot.png"))
draw_plate((40, 40, 45), (20, 20, 25)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "carbon_plate.png"))
draw_plate((50, 50, 55), (30, 30, 35)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "carbon_mesh.png"))
draw_plate((35, 35, 40), (15, 15, 20)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "carbon_fibre.png"))

# Iridium
draw_plate((190, 210, 225), (120, 140, 160)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "iridium_plate.png"))
draw_plate((150, 180, 200), (90, 120, 140)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "reinforced_iridium_plate.png"))
draw_plate((170, 190, 205), (100, 120, 140)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "iridium_ingot.png"))
draw_plate((140, 160, 175), (80, 100, 120)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "raw_iridium.png"))

# Sunnarium
draw_plate((255, 225, 60), (180, 150, 20)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "sunnarium.png"))
draw_plate((255, 210, 40), (190, 140, 10)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "sunnarium_plate.png"))
draw_plate((255, 240, 100), (180, 160, 40)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "sunnarium_part.png"))

# Upgrades
def draw_upgrade(color):
    img, d = draw_item_base(None, None)
    d.rectangle([3, 3, 12, 12], fill=(70, 70, 75), outline=(40, 40, 45))
    d.rectangle([5, 5, 10, 10], fill=color, outline=(255, 255, 255))
    return img

draw_upgrade((40, 180, 220)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "overclocker_upgrade.png"))
draw_upgrade((220, 140, 30)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "energy_storage_upgrade.png"))
draw_upgrade((160, 40, 220)).save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", "transformer_upgrade.png"))

print("All quality pixel-art textures and authentic GUIs generated!")
