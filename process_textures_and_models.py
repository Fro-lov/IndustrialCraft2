import os
import shutil
import zipfile
import json
from PIL import Image

src_ic2_jar = r'E:\minecraft\ATM\assets\original\ic2\industrialcraft-2-2.2.827-experimental.jar'
src_ic2_blocks = r'E:\minecraft\ATM\assets\original\ic2\textures\blocks'
src_ic2_items = r'E:\minecraft\ATM\assets\original\ic2\textures\items'
src_ic2_gui = r'E:\minecraft\ATM\assets\original\ic2\textures\gui'
src_ic2_sounds = r'E:\minecraft\ATM\assets\original\ic2\sounds'

src_loli_blocks = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\blocks'
src_loli_items = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\items'
src_loli_gui = r'E:\minecraft\ATM\assets\loli\lolienergistics\textures\gui'

dst_base = r'E:\minecraft\myMods\onter_ic2\src\main\resources\assets\onter_ic2'
out_block = os.path.join(dst_base, 'textures', 'block')
out_item = os.path.join(dst_base, 'textures', 'item')
out_gui = os.path.join(dst_base, 'textures', 'gui', 'container')
out_sounds = os.path.join(dst_base, 'sounds')

os.makedirs(out_block, exist_ok=True)
os.makedirs(out_item, exist_ok=True)
os.makedirs(out_gui, exist_ok=True)
os.makedirs(out_sounds, exist_ok=True)

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

    tiles[0].save(os.path.join(out_block, f"{prefix}_bottom.png"))
    tiles[1].save(os.path.join(out_block, f"{prefix}_top.png"))
    tiles[2].save(os.path.join(out_block, f"{prefix}_back.png"))
    tiles[3].save(os.path.join(out_block, f"{prefix}_front.png"))
    tiles[4].save(os.path.join(out_block, f"{prefix}_side.png"))

    if len(tiles) > 9:
        tiles[9].save(os.path.join(out_block, f"{prefix}_front_active.png"))
    else:
        tiles[3].save(os.path.join(out_block, f"{prefix}_front_active.png"))

    print(f"Sliced {prefix}")

# Base Machines
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockMacerator.png'), 'macerator')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockElecFurnace.png'), 'electric_furnace')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockCompressor.png'), 'compressor')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockExtractor.png'), 'extractor')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockMetalFormer.png'), 'metal_former')
slice_machine(os.path.join(src_ic2_blocks, 'machine', 'blockIronFurnace.png'), 'generator')

# Advanced & Max Machines (x6 and x12 from LoliEnergistics)
loli_mach = os.path.join(src_loli_blocks, 'machine')
slice_machine(os.path.join(loli_mach, 'MaceratorX6.png'), 'advanced_macerator')
slice_machine(os.path.join(loli_mach, 'MaceratorX12.png'), 'max_macerator')
slice_machine(os.path.join(loli_mach, 'ElectricFurnaceX6.png'), 'advanced_electric_furnace')
slice_machine(os.path.join(loli_mach, 'ElectricFurnaceX12.png'), 'max_electric_furnace')
slice_machine(os.path.join(loli_mach, 'CompressorX6.png'), 'advanced_compressor')
slice_machine(os.path.join(loli_mach, 'CompressorX12.png'), 'max_compressor')
slice_machine(os.path.join(loli_mach, 'ExtractorX6.png'), 'advanced_extractor')
slice_machine(os.path.join(loli_mach, 'ExtractorX12.png'), 'max_extractor')
slice_machine(os.path.join(loli_mach, 'MetalFormerX6.png'), 'advanced_metal_former')
slice_machine(os.path.join(loli_mach, 'MetalFormerX12.png'), 'max_metal_former')

# 2. CASINGS
casing = Image.open(os.path.join(src_ic2_blocks, 'machine', 'blockMachine.png')).convert('RGBA')
casing.save(os.path.join(out_block, 'machine_casing.png'))
adv_casing = Image.open(os.path.join(src_ic2_blocks, 'machine', 'blockAdvMachine.png')).convert('RGBA')
adv_casing.save(os.path.join(out_block, 'advanced_machine_casing.png'))

# 3. SOLAR PANELS
def copy_solar(prefix, loli_prefix):
    top_p = os.path.join(src_loli_blocks, f"{loli_prefix}_top.png")
    side_p = os.path.join(src_loli_blocks, f"{loli_prefix}_side.png")
    bot_p = os.path.join(src_loli_blocks, f"{loli_prefix}_bottom.png")
    if os.path.exists(top_p):
        Image.open(top_p).convert('RGBA').save(os.path.join(out_block, f"{prefix}_top.png"))
    if os.path.exists(side_p):
        Image.open(side_p).convert('RGBA').save(os.path.join(out_block, f"{prefix}_side.png"))
    if os.path.exists(bot_p):
        Image.open(bot_p).convert('RGBA').save(os.path.join(out_block, f"{prefix}_bottom.png"))
    elif os.path.exists(top_p):
        Image.open(top_p).convert('RGBA').save(os.path.join(out_block, f"{prefix}_bottom.png"))
    print(f"Copied solar {prefix} from {loli_prefix}")

copy_solar('solar_panel', 'sp')
copy_solar('advanced_solar_panel', 'asp')
copy_solar('hybrid_solar_panel', 'hsp')
copy_solar('ultimate_hybrid_solar_panel', 'usp')
copy_solar('quantum_solar_panel', 'qsp')

# 4. STORAGE BLOCKS (Extract 6 faces: bottom, top, back, front/output, side)
def slice_storage(src_sheet, prefix):
    if not os.path.exists(src_sheet):
        return
    im = Image.open(src_sheet).convert('RGBA')
    w, h = im.size
    tile_w = h
    tiles = [im.crop((i * tile_w, 0, (i + 1) * tile_w, h)) for i in range(w // tile_w)]
    tiles[0].save(os.path.join(out_block, f"{prefix}_bottom.png"))
    tiles[1].save(os.path.join(out_block, f"{prefix}_top.png"))
    tiles[2].save(os.path.join(out_block, f"{prefix}_back.png"))
    tiles[3].save(os.path.join(out_block, f"{prefix}_output.png"))
    tiles[3].save(os.path.join(out_block, f"{prefix}_front.png"))
    tiles[4].save(os.path.join(out_block, f"{prefix}_side.png"))
    print(f"Sliced storage {prefix}")

wiring_dir = os.path.join(src_ic2_blocks, 'wiring')
slice_storage(os.path.join(wiring_dir, 'blockBatBox.png'), 'batbox')
slice_storage(os.path.join(wiring_dir, 'blockCESU.png'), 'cesu')
slice_storage(os.path.join(wiring_dir, 'blockMFE.png'), 'mfe')
slice_storage(os.path.join(wiring_dir, 'blockMFSU.png'), 'mfsu')

# 5. CABLES & ITEMS FROM JAR
with zipfile.ZipFile(src_ic2_jar, 'r') as z:
    # Cables Item & Block textures
    cable_extractions = {
        'copper_cable_uninsulated.png': ('assets/ic2/textures/items/itemCableO.png', 'assets/ic2/textures/blocks/wiring/cable/blockCableO.png'),
        'copper_cable.png': ('assets/ic2/textures/items/itemCable.png', 'assets/ic2/textures/blocks/wiring/cable/blockCable.png'),
        'gold_cable_uninsulated.png': ('assets/ic2/textures/items/itemGoldCable.png', 'assets/ic2/textures/blocks/wiring/cable/blockGoldCable.png'),
        'gold_cable_1x.png': ('assets/ic2/textures/items/itemGoldCableI.png', 'assets/ic2/textures/blocks/wiring/cable/blockGoldCableI.png'),
        'gold_cable_2x.png': ('assets/ic2/textures/items/itemGoldCableII.png', 'assets/ic2/textures/blocks/wiring/cable/blockGoldCableII.png'),
        'hv_cable_uninsulated.png': ('assets/ic2/textures/items/itemIronCable.png', 'assets/ic2/textures/blocks/wiring/cable/blockIronCable.png'),
        'hv_cable_1x.png': ('assets/ic2/textures/items/itemIronCableI.png', 'assets/ic2/textures/blocks/wiring/cable/blockIronCableI.png'),
        'hv_cable_2x.png': ('assets/ic2/textures/items/itemIronCableII.png', 'assets/ic2/textures/blocks/wiring/cable/blockIronCableII.png'),
        'hv_cable_3x.png': ('assets/ic2/textures/items/itemIronCableIIII.png', 'assets/ic2/textures/blocks/wiring/cable/blockIronCableIIII.png'),
        'glass_fibre_cable.png': ('assets/ic2/textures/items/itemGlassCable.png', 'assets/ic2/textures/blocks/wiring/cable/blockGlassCable.png'),
    }
    for target_name, (item_src, block_src) in cable_extractions.items():
        if item_src in z.namelist():
            with z.open(item_src) as f:
                Image.open(f).convert('RGBA').save(os.path.join(out_item, target_name))
        if block_src in z.namelist():
            with z.open(block_src) as f:
                Image.open(f).convert('RGBA').save(os.path.join(out_block, target_name))
        else:
            if os.path.exists(os.path.join(out_item, target_name)):
                shutil.copy2(os.path.join(out_item, target_name), os.path.join(out_block, target_name))

    # Wrench item & sound
    if 'assets/ic2/textures/items/itemToolWrench.png' in z.namelist():
        with z.open('assets/ic2/textures/items/itemToolWrench.png') as f:
            Image.open(f).convert('RGBA').save(os.path.join(out_item, 'wrench.png'))
    if 'ic2/sounds/Tools/wrench.ogg' in z.namelist():
        with z.open('ic2/sounds/Tools/wrench.ogg') as f:
            with open(os.path.join(out_sounds, 'wrench.ogg'), 'wb') as out_f:
                out_f.write(f.read())

    # Upgrades
    upgrade_map = {
        'ejector_upgrade.png': 'assets/ic2/textures/items/upgrade/ejectorUpgrade.png',
        'pulling_upgrade.png': 'assets/ic2/textures/items/upgrade/pullingUpgrade.png',
    }
    for target_name, jar_path in upgrade_map.items():
        if jar_path in z.namelist():
            with z.open(jar_path) as f:
                Image.open(f).convert('RGBA').save(os.path.join(out_item, target_name))

# Superconductor cable
im_sc = Image.new("RGBA", (16, 16), (140, 40, 220, 255))
d_sc = Image.new("RGBA", (16, 16), (220, 100, 255, 255))
im_sc.paste(d_sc.crop((4, 4, 12, 12)), (4, 4))
im_sc.save(os.path.join(out_block, 'superconductor_cable.png'))
im_sc.save(os.path.join(out_item, 'superconductor_cable.png'))

# 6. OTHER ITEMS
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
        Image.open(src).convert('RGBA').save(os.path.join(out_item, name))

# 7. GUIS
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
        im = Image.open(src).convert('RGBA')
        im.save(os.path.join(out_gui, name))

# 8. SOUNDS
sound_sources = {
    'macerator_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'MaceratorOp.ogg'),
    'electro_furnace_loop.ogg': os.path.join(src_ic2_sounds, 'Machines', 'Electro Furnace', 'ElectroFurnaceLoop.ogg'),
    'compressor_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'CompressorOp.ogg'),
    'extractor_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'ExtractorOp.ogg'),
    'generator_op.ogg': os.path.join(src_ic2_sounds, 'Machines', 'IronFurnaceOp.ogg'),
}
for name, src in sound_sources.items():
    if os.path.exists(src):
        shutil.copy2(src, os.path.join(out_sounds, name))

# Generate sounds.json
sounds_json = {
    "macerator_op": {"sounds": ["onter_ic2:macerator_op"], "subtitle": "subtitles.onter_ic2.macerator_op"},
    "electro_furnace_loop": {"sounds": ["onter_ic2:electro_furnace_loop"], "subtitle": "subtitles.onter_ic2.electro_furnace_loop"},
    "compressor_op": {"sounds": ["onter_ic2:compressor_op"], "subtitle": "subtitles.onter_ic2.compressor_op"},
    "extractor_op": {"sounds": ["onter_ic2:extractor_op"], "subtitle": "subtitles.onter_ic2.extractor_op"},
    "generator_op": {"sounds": ["onter_ic2:generator_op"], "subtitle": "subtitles.onter_ic2.generator_op"},
    "wrench": {"sounds": ["onter_ic2:wrench"], "subtitle": "subtitles.onter_ic2.wrench"}
}
with open(os.path.join(dst_base, 'sounds.json'), 'w', encoding='utf-8') as f:
    json.dump(sounds_json, f, indent=2)

print("All textures, GUIs, models, and sounds processed successfully!")
