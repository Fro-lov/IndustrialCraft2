import os
import json

base_dir = r"E:\minecraft\myMods\onter_ic2"
recipe_dir = os.path.join(base_dir, "src", "main", "resources", "data", "onter_ic2", "recipe")
lang_dir = os.path.join(base_dir, "src", "main", "resources", "assets", "onter_ic2", "lang")
os.makedirs(recipe_dir, exist_ok=True)
os.makedirs(lang_dir, exist_ok=True)

ru_dict = {
  "itemGroup.onter_ic2": "Onter IC2 (IndustrialCraft)",

  "block.onter_ic2.macerator": "Дробитель",
  "block.onter_ic2.electric_furnace": "Электропечь",
  "block.onter_ic2.compressor": "Компрессор",
  "block.onter_ic2.extractor": "Экстрактор",
  "block.onter_ic2.metal_former": "Металлоформовщик",

  "block.onter_ic2.advanced_macerator": "Продвинутый дробитель (x6)",
  "block.onter_ic2.advanced_electric_furnace": "Продвинутая электропечь (x6)",
  "block.onter_ic2.advanced_compressor": "Продвинутый компрессор (x6)",
  "block.onter_ic2.advanced_extractor": "Продвинутый экстрактор (x6)",
  "block.onter_ic2.advanced_metal_former": "Продвинутый металлоформовщик (x6)",

  "block.onter_ic2.max_macerator": "Максимальный дробитель (x12)",
  "block.onter_ic2.max_electric_furnace": "Максимальная электропечь (x12)",
  "block.onter_ic2.max_compressor": "Максимальный компрессор (x12)",
  "block.onter_ic2.max_extractor": "Максимальный экстрактор (x12)",
  "block.onter_ic2.max_metal_former": "Максимальный металлоформовщик (x12)",

  "block.onter_ic2.generator": "Генератор",
  "block.onter_ic2.solar_panel": "Солнечная панель",
  "block.onter_ic2.advanced_solar_panel": "Продвинутая панель",
  "block.onter_ic2.hybrid_solar_panel": "Гибридная панель",
  "block.onter_ic2.ultimate_hybrid_solar_panel": "Ультимейт панель",
  "block.onter_ic2.quantum_solar_panel": "Квантовая панель",

  "block.onter_ic2.batbox": "BatBox",
  "block.onter_ic2.cesu": "CESU",
  "block.onter_ic2.mfe": "MFE",
  "block.onter_ic2.mfsu": "MFSU",

  "block.onter_ic2.copper_cable_uninsulated": "Медный провод (без изоляции)",
  "block.onter_ic2.copper_cable": "Медный изолированный провод",
  "block.onter_ic2.gold_cable_uninsulated": "Золотой провод (без изоляции)",
  "block.onter_ic2.gold_cable_1x": "Золотой провод (1x изоляция)",
  "block.onter_ic2.gold_cable_2x": "Золотой провод (2x изоляция)",
  "block.onter_ic2.hv_cable_uninsulated": "Высоковольтный провод (без изоляции)",
  "block.onter_ic2.hv_cable_1x": "Высоковольтный провод (1x изоляция)",
  "block.onter_ic2.hv_cable_2x": "Высоковольтный провод (2x изоляция)",
  "block.onter_ic2.hv_cable_3x": "Высоковольтный провод (3x изоляция)",
  "block.onter_ic2.glass_fibre_cable": "Стекловолоконный провод",
  "block.onter_ic2.superconductor_cable": "Сверхпроводящий кабель (Без потерь)",

  "item.onter_ic2.wrench": "Гаечный ключ (Wrench)",

  "item.onter_ic2.rubber": "Резина",
  "item.onter_ic2.sticky_resin": "Латекс",
  "item.onter_ic2.electronic_circuit": "Электронная схема",
  "item.onter_ic2.advanced_circuit": "Улучшенная схема",
  "item.onter_ic2.mixed_metal_ingot": "Слиток композитного сплава",
  "item.onter_ic2.advanced_alloy": "Композит",
  "item.onter_ic2.carbon_fibre": "Углеволокно",
  "item.onter_ic2.carbon_mesh": "Углепластик",
  "item.onter_ic2.carbon_plate": "Углепластиковая пластина",

  "item.onter_ic2.raw_iridium": "Необработанный иридий",
  "item.onter_ic2.iridium_ingot": "Иридиевый слиток",
  "item.onter_ic2.iridium_plate": "Иридиевая пластина",
  "item.onter_ic2.reinforced_iridium_plate": "Укрепленная иридиевая пластина",

  "item.onter_ic2.sunnarium_part": "Кусочек саннариума",
  "item.onter_ic2.sunnarium": "Саннариум",
  "item.onter_ic2.sunnarium_plate": "Саннариумовая пластина",

  "item.onter_ic2.re_battery": "Аккумулятор (RE-Battery)",
  "item.onter_ic2.energy_crystal": "Энергетический кристалл",
  "item.onter_ic2.lapotron_crystal": "Лазуротроновый кристалл",

  "item.onter_ic2.overclocker_upgrade": "Ускоритель (Overclocker)",
  "item.onter_ic2.energy_storage_upgrade": "Улучшение 'Хранилище энергии'",
  "item.onter_ic2.transformer_upgrade": "Улучшение 'Трансформатор'",
  "item.onter_ic2.ejector_upgrade": "Выталкиватель (Ejector Upgrade)",
  "item.onter_ic2.pulling_upgrade": "Затягиватель (Pulling Upgrade)",

  "tooltip.onter_ic2.energy": "§eЭнергия: §f%d / %d FE §7(§a%d / %d EU§7)",
  "tooltip.onter_ic2.wrench_dismantle": "§7ПКМ: Вращение стороны выхода\n§7Shift + ПКМ: Безопасный демонтаж механизма",
  "tooltip.onter_ic2.upgrade_ejector": "§7Автоматически выталкивает готовую продукцию в соседний инвентарь.\n§eShift + ПКМ в воздухе/блоку для настройки стороны.",
  "tooltip.onter_ic2.upgrade_pulling": "§7Автоматически затягивает ресурсы из соседнего инвентаря.\n§eShift + ПКМ в воздухе/блоку для настройки стороны.",
  "message.onter_ic2.solar_status": "§eЭнергия: §f%d / %d FE §7| Генерация: §a%d FE/t (%d EU/t)",
  "message.onter_ic2.metal_former_mode": "§eРежим металлоформовщика: §a%s",
  "gui.onter_ic2.metal_former.mode.extruding": "Выдавливание (Провода)",
  "gui.onter_ic2.metal_former.mode.rolling": "Прокатка (Пластины)",
  "gui.onter_ic2.metal_former.mode.cutting": "Резка (Оболочки/Полосы)"
}

en_dict = {
  "itemGroup.onter_ic2": "Onter IC2 (IndustrialCraft)",

  "block.onter_ic2.macerator": "Macerator",
  "block.onter_ic2.electric_furnace": "Electric Furnace",
  "block.onter_ic2.compressor": "Compressor",
  "block.onter_ic2.extractor": "Extractor",
  "block.onter_ic2.metal_former": "Metal Former",

  "block.onter_ic2.advanced_macerator": "Advanced Macerator (x6)",
  "block.onter_ic2.advanced_electric_furnace": "Advanced Electric Furnace (x6)",
  "block.onter_ic2.advanced_compressor": "Advanced Compressor (x6)",
  "block.onter_ic2.advanced_extractor": "Advanced Extractor (x6)",
  "block.onter_ic2.advanced_metal_former": "Advanced Metal Former (x6)",

  "block.onter_ic2.max_macerator": "Maximum Macerator (x12)",
  "block.onter_ic2.max_electric_furnace": "Maximum Electric Furnace (x12)",
  "block.onter_ic2.max_compressor": "Maximum Compressor (x12)",
  "block.onter_ic2.max_extractor": "Maximum Extractor (x12)",
  "block.onter_ic2.max_metal_former": "Maximum Metal Former (x12)",

  "block.onter_ic2.generator": "Generator",
  "block.onter_ic2.solar_panel": "Solar Panel",
  "block.onter_ic2.advanced_solar_panel": "Advanced Solar Panel",
  "block.onter_ic2.hybrid_solar_panel": "Hybrid Solar Panel",
  "block.onter_ic2.ultimate_hybrid_solar_panel": "Ultimate Hybrid Solar Panel",
  "block.onter_ic2.quantum_solar_panel": "Quantum Solar Panel",

  "block.onter_ic2.batbox": "BatBox",
  "block.onter_ic2.cesu": "CESU",
  "block.onter_ic2.mfe": "MFE",
  "block.onter_ic2.mfsu": "MFSU",

  "block.onter_ic2.copper_cable_uninsulated": "Copper Cable (Uninsulated)",
  "block.onter_ic2.copper_cable": "Insulated Copper Cable",
  "block.onter_ic2.gold_cable_uninsulated": "Gold Cable (Uninsulated)",
  "block.onter_ic2.gold_cable_1x": "Gold Cable (1x Insulated)",
  "block.onter_ic2.gold_cable_2x": "Gold Cable (2x Insulated)",
  "block.onter_ic2.hv_cable_uninsulated": "HV Cable (Uninsulated)",
  "block.onter_ic2.hv_cable_1x": "HV Cable (1x Insulated)",
  "block.onter_ic2.hv_cable_2x": "HV Cable (2x Insulated)",
  "block.onter_ic2.hv_cable_3x": "HV Cable (3x Insulated)",
  "block.onter_ic2.glass_fibre_cable": "Glass Fibre Cable",
  "block.onter_ic2.superconductor_cable": "Superconductor Cable (Lossless)",

  "item.onter_ic2.wrench": "Wrench",

  "item.onter_ic2.rubber": "Rubber",
  "item.onter_ic2.sticky_resin": "Sticky Resin",
  "item.onter_ic2.electronic_circuit": "Electronic Circuit",
  "item.onter_ic2.advanced_circuit": "Advanced Circuit",
  "item.onter_ic2.mixed_metal_ingot": "Mixed Metal Ingot",
  "item.onter_ic2.advanced_alloy": "Advanced Alloy",
  "item.onter_ic2.carbon_fibre": "Raw Carbon Fibre",
  "item.onter_ic2.carbon_mesh": "Raw Carbon Mesh",
  "item.onter_ic2.carbon_plate": "Carbon Plate",

  "item.onter_ic2.raw_iridium": "Raw Iridium",
  "item.onter_ic2.iridium_ingot": "Iridium Ingot",
  "item.onter_ic2.iridium_plate": "Iridium Plate",
  "item.onter_ic2.reinforced_iridium_plate": "Reinforced Iridium Plate",

  "item.onter_ic2.sunnarium_part": "Sunnarium Part",
  "item.onter_ic2.sunnarium": "Sunnarium",
  "item.onter_ic2.sunnarium_plate": "Sunnarium Plate",

  "item.onter_ic2.re_battery": "RE-Battery",
  "item.onter_ic2.energy_crystal": "Energy Crystal",
  "item.onter_ic2.lapotron_crystal": "Lapotron Crystal",

  "item.onter_ic2.overclocker_upgrade": "Overclocker Upgrade",
  "item.onter_ic2.energy_storage_upgrade": "Energy Storage Upgrade",
  "item.onter_ic2.transformer_upgrade": "Transformer Upgrade",
  "item.onter_ic2.ejector_upgrade": "Ejector Upgrade",
  "item.onter_ic2.pulling_upgrade": "Pulling Upgrade",

  "tooltip.onter_ic2.energy": "§eEnergy: §f%d / %d FE §7(§a%d / %d EU§7)",
  "tooltip.onter_ic2.wrench_dismantle": "§7Right Click: Rotate output face\n§7Shift + Right Click: Safely dismantle machine",
  "tooltip.onter_ic2.upgrade_ejector": "§7Automatically ejects output items into adjacent inventory.\n§eShift + Right Click to configure target direction.",
  "tooltip.onter_ic2.upgrade_pulling": "§7Automatically pulls input items from adjacent inventory.\n§eShift + Right Click to configure source direction.",
  "message.onter_ic2.solar_status": "§eEnergy: §f%d / %d FE §7| Gen: §a%d FE/t (%d EU/t)",
  "message.onter_ic2.metal_former_mode": "§eMetal Former Mode: §a%s",
  "gui.onter_ic2.metal_former.mode.extruding": "Extruding (Cables)",
  "gui.onter_ic2.metal_former.mode.rolling": "Rolling (Plates)",
  "gui.onter_ic2.metal_former.mode.cutting": "Cutting (Casings)"
}

with open(os.path.join(lang_dir, "ru_ru.json"), "w", encoding="utf-8") as f:
    json.dump(ru_dict, f, ensure_ascii=False, indent=2)

with open(os.path.join(lang_dir, "en_us.json"), "w", encoding="utf-8") as f:
    json.dump(en_dict, f, ensure_ascii=False, indent=2)

# Recipes to generate
new_recipes = [
    # Wrench
    {
        "file": "wrench.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "C C",
                "CCC",
                " C "
            ],
            "key": {
                "C": {"tag": "c:ingots/copper"}
            },
            "result": {"id": "onter_ic2:wrench", "count": 1}
        }
    },
    # Uninsulated Copper Cable (from 3 copper ingots)
    {
        "file": "copper_cable_uninsulated.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["CCC"],
            "key": {
                "C": {"tag": "c:ingots/copper"}
            },
            "result": {"id": "onter_ic2:copper_cable_uninsulated", "count": 6}
        }
    },
    # Insulated Copper Cable (from uninsulated + rubber)
    {
        "file": "copper_cable_insulated_from_rubber.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:copper_cable_uninsulated"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:copper_cable", "count": 1}
        }
    },
    # Uninsulated Gold Cable
    {
        "file": "gold_cable_uninsulated.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["GGG"],
            "key": {
                "G": {"tag": "c:ingots/gold"}
            },
            "result": {"id": "onter_ic2:gold_cable_uninsulated", "count": 4}
        }
    },
    # Gold Cable 1x
    {
        "file": "gold_cable_1x.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:gold_cable_uninsulated"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:gold_cable_1x", "count": 1}
        }
    },
    # Gold Cable 2x
    {
        "file": "gold_cable_2x.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:gold_cable_1x"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:gold_cable_2x", "count": 1}
        }
    },
    # Uninsulated HV Cable
    {
        "file": "hv_cable_uninsulated.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["III"],
            "key": {
                "I": {"tag": "c:ingots/iron"}
            },
            "result": {"id": "onter_ic2:hv_cable_uninsulated", "count": 4}
        }
    },
    # HV Cable 1x
    {
        "file": "hv_cable_1x.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:hv_cable_uninsulated"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:hv_cable_1x", "count": 1}
        }
    },
    # HV Cable 2x
    {
        "file": "hv_cable_2x.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:hv_cable_1x"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:hv_cable_2x", "count": 1}
        }
    },
    # HV Cable 3x
    {
        "file": "hv_cable_3x.json",
        "data": {
            "type": "minecraft:crafting_shapeless",
            "ingredients": [
                {"item": "onter_ic2:hv_cable_2x"},
                {"item": "onter_ic2:rubber"}
            ],
            "result": {"id": "onter_ic2:hv_cable_3x", "count": 1}
        }
    },
    # Glass Fibre Cable
    {
        "file": "glass_fibre_cable.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "GGG",
                "RER",
                "GGG"
            ],
            "key": {
                "G": {"tag": "c:glass_blocks"},
                "R": {"tag": "c:dusts/redstone"},
                "E": {"item": "onter_ic2:energy_crystal"}
            },
            "result": {"id": "onter_ic2:glass_fibre_cable", "count": 6}
        }
    },
    # Superconductor Cable
    {
        "file": "superconductor_cable.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "SGS",
                "GLG",
                "SGS"
            ],
            "key": {
                "S": {"item": "onter_ic2:sunnarium"},
                "G": {"item": "onter_ic2:glass_fibre_cable"},
                "L": {"item": "onter_ic2:lapotron_crystal"}
            },
            "result": {"id": "onter_ic2:superconductor_cable", "count": 4}
        }
    },
    # Ejector Upgrade
    {
        "file": "ejector_upgrade.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                " C ",
                " P ",
                " C "
            ],
            "key": {
                "C": {"item": "onter_ic2:copper_cable"},
                "P": {"item": "minecraft:piston"}
            },
            "result": {"id": "onter_ic2:ejector_upgrade", "count": 1}
        }
    },
    # Pulling Upgrade
    {
        "file": "pulling_upgrade.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                " C ",
                " P ",
                " C "
            ],
            "key": {
                "C": {"item": "onter_ic2:copper_cable"},
                "P": {"item": "minecraft:sticky_piston"}
            },
            "result": {"id": "onter_ic2:pulling_upgrade", "count": 1}
        }
    },
    # Advanced Macerator (x6)
    {
        "file": "advanced_macerator.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OCO",
                "AMA",
                "OCO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "M": {"item": "onter_ic2:macerator"}
            },
            "result": {"id": "onter_ic2:advanced_macerator", "count": 1}
        }
    },
    # Advanced Electric Furnace (x6)
    {
        "file": "advanced_electric_furnace.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OCO",
                "AMA",
                "OCO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "M": {"item": "onter_ic2:electric_furnace"}
            },
            "result": {"id": "onter_ic2:advanced_electric_furnace", "count": 1}
        }
    },
    # Advanced Compressor (x6)
    {
        "file": "advanced_compressor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OCO",
                "AMA",
                "OCO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "M": {"item": "onter_ic2:compressor"}
            },
            "result": {"id": "onter_ic2:advanced_compressor", "count": 1}
        }
    },
    # Advanced Extractor (x6)
    {
        "file": "advanced_extractor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OCO",
                "AMA",
                "OCO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "M": {"item": "onter_ic2:extractor"}
            },
            "result": {"id": "onter_ic2:advanced_extractor", "count": 1}
        }
    },
    # Advanced Metal Former (x6)
    {
        "file": "advanced_metal_former.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OCO",
                "AMA",
                "OCO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "M": {"item": "onter_ic2:metal_former"}
            },
            "result": {"id": "onter_ic2:advanced_metal_former", "count": 1}
        }
    },
    # Max Macerator (x12)
    {
        "file": "max_macerator.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OLO",
                "PMP",
                "OLO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "P": {"item": "onter_ic2:reinforced_iridium_plate"},
                "M": {"item": "onter_ic2:advanced_macerator"}
            },
            "result": {"id": "onter_ic2:max_macerator", "count": 1}
        }
    },
    # Max Electric Furnace (x12)
    {
        "file": "max_electric_furnace.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OLO",
                "PMP",
                "OLO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "P": {"item": "onter_ic2:reinforced_iridium_plate"},
                "M": {"item": "onter_ic2:advanced_electric_furnace"}
            },
            "result": {"id": "onter_ic2:max_electric_furnace", "count": 1}
        }
    },
    # Max Compressor (x12)
    {
        "file": "max_compressor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OLO",
                "PMP",
                "OLO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "P": {"item": "onter_ic2:reinforced_iridium_plate"},
                "M": {"item": "onter_ic2:advanced_compressor"}
            },
            "result": {"id": "onter_ic2:max_compressor", "count": 1}
        }
    },
    # Max Extractor (x12)
    {
        "file": "max_extractor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OLO",
                "PMP",
                "OLO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "P": {"item": "onter_ic2:reinforced_iridium_plate"},
                "M": {"item": "onter_ic2:advanced_extractor"}
            },
            "result": {"id": "onter_ic2:max_extractor", "count": 1}
        }
    },
    # Max Metal Former (x12)
    {
        "file": "max_metal_former.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [
                "OLO",
                "PMP",
                "OLO"
            ],
            "key": {
                "O": {"item": "onter_ic2:overclocker_upgrade"},
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "P": {"item": "onter_ic2:reinforced_iridium_plate"},
                "M": {"item": "onter_ic2:advanced_metal_former"}
            },
            "result": {"id": "onter_ic2:max_metal_former", "count": 1}
        }
    },
]

for r in new_recipes:
    file_path = os.path.join(recipe_dir, r["file"])
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(r["data"], f, indent=2)

print(f"Generated {len(new_recipes)} new recipes and updated lang files.")
