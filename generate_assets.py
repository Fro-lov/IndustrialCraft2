import os
import json
from PIL import Image, ImageDraw

base_dir = r"e:\minecraft\myMods\onter_ic2\src\main\resources"

dirs = [
    os.path.join(base_dir, "assets", "onter_ic2", "blockstates"),
    os.path.join(base_dir, "assets", "onter_ic2", "models", "block"),
    os.path.join(base_dir, "assets", "onter_ic2", "models", "item"),
    os.path.join(base_dir, "assets", "onter_ic2", "textures", "block"),
    os.path.join(base_dir, "assets", "onter_ic2", "textures", "item"),
    os.path.join(base_dir, "assets", "onter_ic2", "textures", "gui", "container"),
    os.path.join(base_dir, "data", "onter_ic2", "loot_table", "blocks"),
    os.path.join(base_dir, "data", "onter_ic2", "recipe"),
]

for d in dirs:
    os.makedirs(d, exist_ok=True)

blocks = [
    'macerator', 'electric_furnace', 'compressor', 'extractor', 'metal_former',
    'generator', 'solar_panel', 'advanced_solar_panel', 'hybrid_solar_panel',
    'ultimate_hybrid_solar_panel', 'quantum_solar_panel',
    'batbox', 'cesu', 'mfe', 'mfsu',
    'copper_cable', 'gold_cable', 'hv_cable', 'glass_fibre_cable', 'superconductor_cable'
]

items = [
    'rubber', 'sticky_resin', 'electronic_circuit', 'advanced_circuit',
    'mixed_metal_ingot', 'advanced_alloy', 'carbon_fibre', 'carbon_mesh', 'carbon_plate',
    'raw_iridium', 'iridium_ingot', 'iridium_plate', 'reinforced_iridium_plate',
    'sunnarium_part', 'sunnarium', 'sunnarium_plate',
    're_battery', 'energy_crystal', 'lapotron_crystal',
    'overclocker_upgrade', 'energy_storage_upgrade', 'transformer_upgrade'
]

# Colors for block textures
block_colors = {
    'macerator': (80, 80, 85),
    'electric_furnace': (100, 90, 85),
    'compressor': (70, 75, 80),
    'extractor': (85, 80, 70),
    'metal_former': (75, 85, 90),
    'generator': (60, 60, 60),
    'solar_panel': (40, 50, 90),
    'advanced_solar_panel': (50, 70, 130),
    'hybrid_solar_panel': (70, 30, 110),
    'ultimate_hybrid_solar_panel': (30, 110, 130),
    'quantum_solar_panel': (130, 30, 140),
    'batbox': (120, 85, 45),
    'cesu': (130, 110, 50),
    'mfe': (60, 80, 120),
    'mfsu': (40, 100, 150),
    'copper_cable': (180, 100, 50),
    'gold_cable': (220, 180, 40),
    'hv_cable': (160, 160, 170),
    'glass_fibre_cable': (200, 240, 255),
    'superconductor_cable': (160, 50, 240)
}

# Item colors
item_colors = {
    'rubber': (30, 30, 30),
    'sticky_resin': (220, 160, 30),
    'electronic_circuit': (40, 150, 50),
    'advanced_circuit': (180, 40, 40),
    'mixed_metal_ingot': (140, 140, 150),
    'advanced_alloy': (180, 150, 130),
    'carbon_fibre': (40, 40, 40),
    'carbon_mesh': (50, 50, 50),
    'carbon_plate': (35, 35, 35),
    'raw_iridium': (160, 180, 190),
    'iridium_ingot': (200, 220, 230),
    'iridium_plate': (190, 210, 220),
    'reinforced_iridium_plate': (150, 180, 200),
    'sunnarium_part': (240, 220, 60),
    'sunnarium': (255, 235, 80),
    'sunnarium_plate': (255, 215, 50),
    're_battery': (180, 40, 40),
    'energy_crystal': (220, 40, 60),
    'lapotron_crystal': (40, 80, 230),
    'overclocker_upgrade': (50, 180, 200),
    'energy_storage_upgrade': (200, 140, 40),
    'transformer_upgrade': (140, 60, 200)
}

# Generate Blocks
for b in blocks:
    # Blockstate
    bs = {"variants": {"": {"model": f"onter_ic2:block/{b}"}}}
    with open(os.path.join(base_dir, "assets", "onter_ic2", "blockstates", f"{b}.json"), "w") as f:
        json.dump(bs, f, indent=2)

    # Model Block
    bm = {"parent": "minecraft:block/cube_all", "textures": {"all": f"onter_ic2:block/{b}"}}
    with open(os.path.join(base_dir, "assets", "onter_ic2", "models", "block", f"{b}.json"), "w") as f:
        json.dump(bm, f, indent=2)

    # Model Item
    bim = {"parent": f"onter_ic2:block/{b}"}
    with open(os.path.join(base_dir, "assets", "onter_ic2", "models", "item", f"{b}.json"), "w") as f:
        json.dump(bim, f, indent=2)

    # Loot table
    lt = {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1,
            "bonus_rolls": 0,
            "entries": [{"type": "minecraft:item", "name": f"onter_ic2:{b}"}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]
        }]
    }
    with open(os.path.join(base_dir, "data", "onter_ic2", "loot_table", "blocks", f"{b}.json"), "w") as f:
        json.dump(lt, f, indent=2)

    # Create Texture 16x16
    c = block_colors.get(b, (100, 100, 100))
    img = Image.new("RGBA", (16, 16), c)
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, 15, 15], outline=(min(255, c[0]+30), min(255, c[1]+30), min(255, c[2]+30)))
    draw.rectangle([1, 1, 14, 14], outline=(max(0, c[0]-30), max(0, c[1]-30), max(0, c[2]-30)))
    img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "block", f"{b}.png"))

# Generate Items
for item in items:
    im = {"parent": "minecraft:item/generated", "textures": {"layer0": f"onter_ic2:item/{item}"}}
    with open(os.path.join(base_dir, "assets", "onter_ic2", "models", "item", f"{item}.json"), "w") as f:
        json.dump(im, f, indent=2)

    c = item_colors.get(item, (120, 120, 120))
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw.ellipse([2, 2, 13, 13], fill=c, outline=(min(255, c[0]+40), min(255, c[1]+40), min(255, c[2]+40)))
    img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "item", f"{item}.png"))

# Generate GUI backgrounds (256x256)
gui_colors = {
    'machine': (198, 198, 198),
    'generator': (198, 198, 198),
    'storage': (198, 198, 198)
}

for g in gui_colors:
    img = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # background box 176x166
    draw.rectangle([0, 0, 175, 165], fill=(198, 198, 198), outline=(55, 55, 55))
    # progress/energy indicator sprite sheet at right
    draw.rectangle([176, 0, 230, 30], fill=(255, 200, 50))
    img.save(os.path.join(base_dir, "assets", "onter_ic2", "textures", "gui", "container", f"{g}.png"))

print("All assets generated successfully!")
