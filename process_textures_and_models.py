import os
import shutil
from PIL import Image

src_ic2_blocks = r'E:\minecraft\ATM\assets\original\ic2\textures\blocks'
src_ic2_items = r'E:\minecraft\ATM\assets\original\ic2\textures\items'
src_ic2_gui = r'E:\minecraft\ATM\assets\original\ic2\textures\gui'
src_ic2_sounds = r'E:\minecraft\ATM\assets\original\ic2\sounds'

src_loli_blocks = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\blocks'
src_loli_items = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\items'
src_loli_gui = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\gui'

dst_base = r'E:\minecraft\myMods\onter_ic2\src\main\resources\assets\onter_ic2'

# 1. SLICE MACHINE TEXTURES (384x32 -> 32x32 tiles)
def slice_machine(src_sheet, prefix):
    if not os.path.exists(src_sheet):
        print(f"Missing {src_sheet}")
        return
    im = Image.open(src_sheet).convert('RGBA')
    w, h = im.size
    tile_w = h
    tiles = []
    for i in range(w // tile_w):
        tile = im.crop((i * tile_w, 0, (i + 1) * tile_w, h))
        tiles.append(tile)

    out_dir = os.path.join(dst_base, 'textures', 'block')
    os.makedirs(out_dir, exist_ok=True)

    tiles[0].save(os.path.join(out_dir, f"{prefix}_bottom.png"))
    tiles[1].save(os.path.join(out_dir, f"{prefix}_top.png"))
    tiles[2].save(os.path.join(out_dir, f"{prefix}_back.png"))
    tiles[3].save(os.path.join(out_dir, f"{prefix}_front.png"))
    tiles[4].save(os.path.join(out_dir, f"{prefix}_side.png"))

    if len(tiles) > 9:
        tiles[9].save(os.path.join(out_dir, f"{prefix}_front_active.png"))
    else:
        tiles[3].save(os.path.join(out_dir, f"{prefix}_front_active.png"))

    print(f"Sliced {prefix}")

# Machines
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockMacerator.png'), 'macerator')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockElecFurnace.png'), 'electric_furnace')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockCompressor.png'), 'compressor')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockExtractor.png'), 'extractor')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockMetalFormer.png'), 'metal_former')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockIronFurnace.png'), 'generator')

# 2. CASINGS
casing = Image.open(os.path.join(src_ic2_blocks, 'machine', 'blockMachine.png')).convert('RGBA')
casing.save(os.path.join(dst_base, 'textures', 'block', 'machine_casing.png'))
adv_casing = Image.open(os.path.join(src_ic2_blocks, 'machine', 'blockAdvMachine.png')).convert('RGBA')
adv_casing.save(os.path.join(dst_base, 'textures', 'block', 'advanced_machine_casing.png'))

# 3. SOLAR PANELS (Base + Advanced tiers from lolienergistics)
def copy_solar(prefix, loli_prefix):
    top_p = os.path.join(src_loli_blocks, f"{loli_prefix}_top.png")
    side_p = os.path.join(src_loli_blocks, f"{loli_prefix}_side.png")
    bot_p = os.path.join(src_loli_blocks, f"{loli_prefix}_bottom.png")
    out_dir = os.path.join(dst_base, 'textures', 'block')
    if os.path.exists(top_p):
        Image.open(top_p).convert('RGBA').save(os.path.join(out_dir, f"{prefix}_top.png"))
    if os.path.exists(side_p):
        Image.open(side_p).convert('RGBA').save(os.path.join(out_dir, f"{prefix}_side.png"))
    if os.path.exists(bot_p):
        Image.open(bot_p).convert('RGBA').save(os.path.join(out_dir, f"{prefix}_bottom.png"))
    elif os.path.exists(top_p):
        Image.open(top_p).convert('RGBA').save(os.path.join(out_dir, f"{prefix}_bottom.png"))
    print(f"Copied solar {prefix} from {loli_prefix}")

copy_solar('solar_panel', 'sp')
copy_solar('advanced_solar_panel', 'asp')
copy_solar('hybrid_solar_panel', 'hsp')
copy_solar('ultimate_hybrid_solar_panel', 'usp')
copy_solar('quantum_solar_panel', 'qsp')

# 4. STORAGE BLOCKS
wiring_dir = os.path.join(src_ic2_blocks, 'wiring')
if os.path.exists(wiring_dir):
    for f in os.listdir(wiring_dir):
        if 'batbox' in f.lower():
            slice_machine(os.path.join(wiring_dir, f), 'batbox')
        elif 'cesu' in f.lower():
            slice_machine(os.path.join(wiring_dir, f), 'cesu')
        elif 'mfe' in f.lower():
            slice_machine(os.path.join(wiring_dir, f), 'mfe')
        elif 'mfsu' in f.lower():
            slice_machine(os.path.join(wiring_dir, f), 'mfsu')

# 5. CABLE TEXTURES
cable_map = {
    'copper_cable.png': os.path.join(src_ic2_blocks, 'wiring', 'cableCopper.png'),
    'gold_cable.png': os.path.join(src_ic2_blocks, 'wiring', 'cableGold.png'),
    'hv_cable.png': os.path.join(src_ic2_blocks, 'wiring', 'cableIron.png'),
    'glass_fibre_cable.png': os.path.join(src_ic2_blocks, 'wiring', 'cableGlass.png'),
}
for name, p in cable_map.items():
    if os.path.exists(p):
        Image.open(p).convert('RGBA').save(os.path.join(dst_base, 'textures', 'block', name))

# Superconductor cable
im_sc = Image.new("RGBA", (16, 16), (140, 40, 220, 255))
d_sc = Image.new("RGBA", (16, 16), (220, 100, 255, 255))
im_sc.paste(d_sc.crop((4, 4, 12, 12)), (4, 4))
im_sc.save(os.path.join(dst_base, 'textures', 'block', 'superconductor_cable.png'))

# 6. ITEMS
item_map = {
    'rubber.png': os.path.join(src_ic2_items, 'itemRubber.png'),
    'sticky_resin.png': os.path.join(src_ic2_items, 'itemHarz.png'),
    'electronic_circuit.png': os.path.join(src_ic2_items, 'itemPartCircuit.png'),
    'advanced_circuit.png': os.path.join(src_ic2_items, 'itemPartCircuitAdv.png'),
    'mixed_metal_ingot.png': os.path.join(src_ic2_items, 'itemPartAlloy.png'),
    'advanced_alloy.png': os.path.join(src_ic2_items, 'itemPartAlloy.png'),
    'carbon_fibre.png': os.path.join(src_ic2_items, 'itemPartCarbonFibre.png'),
    'carbon_mesh.png': os.path.join(src_ic2_items, 'itemPartCarbonMesh.png'),
    'carbon_plate.png': os.path.join(src_ic2_items, 'itemPartCarbonPlate.png'),
    'raw_iridium.png': os.path.join(src_ic2_items, 'itemOreIridium.png'),
    'iridium_ingot.png': os.path.join(src_loli_items, 'asp', 'ingot_iridium.png') if os.path.exists(os.path.join(src_loli_items, 'asp', 'ingot_iridium.png')) else os.path.join(src_ic2_items, 'itemPartIridium.png'),
    'iridium_plate.png': os.path.join(src_loli_items, 'asp', 'iridium_iron_plate.png') if os.path.exists(os.path.join(src_loli_items, 'asp', 'iridium_iron_plate.png')) else os.path.join(src_ic2_items, 'itemPartIridium.png'),
    'reinforced_iridium_plate.png': os.path.join(src_loli_items, 'asp', 'reinforced_iridium_iron_plate.png') if os.path.exists(os.path.join(src_loli_items, 'asp', 'reinforced_iridium_iron_plate.png')) else os.path.join(src_ic2_items, 'itemPartIridium.png'),
    'sunnarium_part.png': os.path.join(src_loli_items, 'asp', 'sunnarium_part.png'),
    'sunnarium.png': os.path.join(src_loli_items, 'asp', 'sunnarium.png'),
    'sunnarium_plate.png': os.path.join(src_loli_items, 'asp', 'sunnarium_alloy.png'),
    're_battery.png': os.path.join(src_ic2_items, 'itemBatRE.0.png'),
    'energy_crystal.png': os.path.join(src_ic2_items, 'itemBatCrystal.0.png'),
    'lapotron_crystal.png': os.path.join(src_ic2_items, 'itemBatLamaCrystal.0.png'),
    'overclocker_upgrade.png': os.path.join(src_ic2_items, 'upgrade', 'overclocker.png'),
    'energy_storage_upgrade.png': os.path.join(src_ic2_items, 'upgrade', 'energyStorage.png'),
    'transformer_upgrade.png': os.path.join(src_ic2_items, 'upgrade', 'transformer.png'),
}
for name, src in item_map.items():
    if os.path.exists(src):
        Image.open(src).convert('RGBA').save(os.path.join(dst_base, 'textures', 'item', name))

# 7. AUTHENTIC IC2 & LOLI GUIS
gui_out = os.path.join(dst_base, 'textures', 'gui', 'container')
os.makedirs(gui_out, exist_ok=True)

gui_map = {
    'electric_furnace.png': os.path.join(src_ic2_gui, 'GUIElecFurnace.png'),
    'macerator.png': os.path.join(src_ic2_gui, 'GUIMacerator.png'),
    'compressor.png': os.path.join(src_ic2_gui, 'GUICompressor.png'),
    'extractor.png': os.path.join(src_ic2_gui, 'GUIExtractor.png'),
    'metal_former.png': os.path.join(src_ic2_gui, 'GUIMetalFormer.png'),
    'generator.png': os.path.join(src_ic2_gui, 'GUIGenerator.png'),
    'storage.png': os.path.join(src_ic2_gui, 'GUIElectricBlock.png'),
    'solar_panel.png': os.path.join(src_loli_gui, 'gui_solar_panel.png'),
}

for name, src in gui_map.items():
    if os.path.exists(src):
        # Convert palette images to clean RGBA
        im = Image.open(src).convert('RGBA')
        im.save(os.path.join(gui_out, name))
        print(f"Copied GUI {name} from {src}")

# 8. SOUNDS
sounds_out = os.path.join(dst_base, 'sounds')
os.makedirs(sounds_out, exist_ok=True)

sound_sources = {
    'macerator_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'MaceratorOp.ogg'),
    'electro_furnace_loop.ogg': os.path.join(src_ic2_sounds, 'Machines', 'Electro Furnace', 'ElectroFurnaceLoop.ogg'),
    'compressor_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'CompressorOp.ogg'),
    'extractor_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'ExtractorOp.ogg'),
    'generator_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'IronFurnaceOp.ogg'),
}

for name, src in sound_sources.items():
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(sounds_out, name))
        print(f"Copied sound {name}")

print("All textures, GUIs, and sounds processed successfully!")
