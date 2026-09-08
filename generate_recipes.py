import os
import json

base_recipe_dir = r"e:\minecraft\myMods\onter_ic2\src\main\resources\data\onter_ic2\recipe"
os.makedirs(base_recipe_dir, exist_ok=True)

# Crafting Recipes
recipes = [
    # Rubber (from Sticky Resin smelting or extractor)
    {
        "file": "rubber_from_smelting_sticky_resin.json",
        "data": {
            "type": "minecraft:smelting",
            "ingredient": {"item": "onter_ic2:sticky_resin"},
            "result": {"id": "onter_ic2:rubber", "count": 1},
            "experience": 0.1,
            "cookingtime": 200
        }
    },
    # Copper Cable
    {
        "file": "copper_cable.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["RRR", "CCC", "RRR"],
            "key": {
                "R": {"item": "onter_ic2:rubber"},
                "C": {"tag": "c:ingots/copper"}
            },
            "result": {"id": "onter_ic2:copper_cable", "count": 6}
        }
    },
    # Electronic Circuit
    {
        "file": "electronic_circuit.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["CCC", "RIR", "CCC"],
            "key": {
                "C": {"item": "onter_ic2:copper_cable"},
                "R": {"tag": "c:dusts/redstone"},
                "I": {"tag": "c:ingots/iron"}
            },
            "result": {"id": "onter_ic2:electronic_circuit", "count": 1}
        }
    },
    # Advanced Circuit
    {
        "file": "advanced_circuit.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["RGR", "ECE", "RGR"],
            "key": {
                "R": {"tag": "c:dusts/redstone"},
                "G": {"tag": "c:dusts/glowstone"},
                "E": {"tag": "c:gems/lapis"},
                "C": {"item": "onter_ic2:electronic_circuit"}
            },
            "result": {"id": "onter_ic2:advanced_circuit", "count": 1}
        }
    },
    # RE-Battery
    {
        "file": "re_battery.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [" C ", "TRT", "TRT"],
            "key": {
                "C": {"item": "onter_ic2:copper_cable"},
                "T": {"tag": "c:ingots/tin"},
                "R": {"tag": "c:dusts/redstone"}
            },
            "result": {"id": "onter_ic2:re_battery", "count": 1}
        }
    },
    # Energy Crystal
    {
        "file": "energy_crystal.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["RRR", "RDR", "RRR"],
            "key": {
                "R": {"tag": "c:dusts/redstone"},
                "D": {"tag": "c:gems/diamond"}
            },
            "result": {"id": "onter_ic2:energy_crystal", "count": 1}
        }
    },
    # Lapotron Crystal
    {
        "file": "lapotron_crystal.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["LCL", "LEL", "LCL"],
            "key": {
                "L": {"tag": "c:gems/lapis"},
                "C": {"item": "onter_ic2:electronic_circuit"},
                "E": {"item": "onter_ic2:energy_crystal"}
            },
            "result": {"id": "onter_ic2:lapotron_crystal", "count": 1}
        }
    },
    # Generator
    {
        "file": "generator.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [" B ", " I ", " F "],
            "key": {
                "B": {"item": "onter_ic2:re_battery"},
                "I": {"tag": "c:ingots/iron"},
                "F": {"item": "minecraft:furnace"}
            },
            "result": {"id": "onter_ic2:generator", "count": 1}
        }
    },
    # Electric Furnace
    {
        "file": "electric_furnace.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": [" C ", " R ", " G "],
            "key": {
                "C": {"item": "onter_ic2:electronic_circuit"},
                "R": {"tag": "c:dusts/redstone"},
                "G": {"item": "onter_ic2:generator"}
            },
            "result": {"id": "onter_ic2:electric_furnace", "count": 1}
        }
    },
    # Macerator
    {
        "file": "macerator.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["FFF", "ICI", " G "],
            "key": {
                "F": {"item": "minecraft:flint"},
                "I": {"tag": "c:ingots/iron"},
                "C": {"item": "onter_ic2:electronic_circuit"},
                "G": {"item": "onter_ic2:generator"}
            },
            "result": {"id": "onter_ic2:macerator", "count": 1}
        }
    },
    # Compressor
    {
        "file": "compressor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["S S", "SCS", "SGS"],
            "key": {
                "S": {"item": "minecraft:stone"},
                "C": {"item": "onter_ic2:electronic_circuit"},
                "G": {"item": "onter_ic2:generator"}
            },
            "result": {"id": "onter_ic2:compressor", "count": 1}
        }
    },
    # Extractor
    {
        "file": "extractor.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["T T", "TCT", "TGT"],
            "key": {
                "T": {"item": "minecraft:tree_tap", "fallback": {"item": "onter_ic2:rubber"}},
                "C": {"item": "onter_ic2:electronic_circuit"},
                "G": {"item": "onter_ic2:generator"}
            },
            "result": {"id": "onter_ic2:extractor", "count": 1}
        }
    },
    # Metal Former
    {
        "file": "metal_former.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["III", "CTC", "III"],
            "key": {
                "I": {"tag": "c:ingots/iron"},
                "C": {"item": "onter_ic2:electronic_circuit"},
                "T": {"item": "minecraft:chest"}
            },
            "result": {"id": "onter_ic2:metal_former", "count": 1}
        }
    },
    # BatBox
    {
        "file": "batbox.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["PPP", "BBB", "CCC"],
            "key": {
                "P": {"tag": "minecraft:planks"},
                "B": {"item": "onter_ic2:re_battery"},
                "C": {"item": "onter_ic2:copper_cable"}
            },
            "result": {"id": "onter_ic2:batbox", "count": 1}
        }
    },
    # CESU
    {
        "file": "cesu.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["CCC", "ABA", "CCC"],
            "key": {
                "C": {"item": "onter_ic2:gold_cable"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "B": {"item": "onter_ic2:batbox"}
            },
            "result": {"id": "onter_ic2:cesu", "count": 1}
        }
    },
    # MFE
    {
        "file": "mfe.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["EEE", "ACA", "EEE"],
            "key": {
                "E": {"item": "onter_ic2:energy_crystal"},
                "A": {"item": "onter_ic2:advanced_circuit"},
                "C": {"item": "onter_ic2:cesu"}
            },
            "result": {"id": "onter_ic2:mfe", "count": 1}
        }
    },
    # MFSU
    {
        "file": "mfsu.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["LAL", "LML", "LAL"],
            "key": {
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "A": {"item": "onter_ic2:advanced_circuit"},
                "M": {"item": "onter_ic2:mfe"}
            },
            "result": {"id": "onter_ic2:mfsu", "count": 1}
        }
    },
    # Solar Panel
    {
        "file": "solar_panel.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["GGG", "CCC", "EGE"],
            "key": {
                "G": {"tag": "c:glass_blocks"},
                "C": {"tag": "c:dusts/coal"},
                "E": {"item": "onter_ic2:electronic_circuit"}
            },
            "result": {"id": "onter_ic2:solar_panel", "count": 1}
        }
    },
    # Advanced Solar Panel
    {
        "file": "advanced_solar_panel.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["GGG", "ASA", "ECE"],
            "key": {
                "G": {"tag": "c:glass_blocks"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "S": {"item": "onter_ic2:solar_panel"},
                "C": {"item": "onter_ic2:advanced_circuit"},
                "E": {"item": "onter_ic2:re_battery"}
            },
            "result": {"id": "onter_ic2:advanced_solar_panel", "count": 1}
        }
    },
    # Hybrid Solar Panel
    {
        "file": "hybrid_solar_panel.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["CCC", "ASA", "ECE"],
            "key": {
                "C": {"item": "onter_ic2:carbon_plate"},
                "A": {"item": "onter_ic2:sunnarium_plate"},
                "S": {"item": "onter_ic2:advanced_solar_panel"},
                "E": {"item": "onter_ic2:energy_crystal"}
            },
            "result": {"id": "onter_ic2:hybrid_solar_panel", "count": 1}
        }
    },
    # Ultimate Hybrid Solar Panel
    {
        "file": "ultimate_hybrid_solar_panel.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["LAL", "HSH", "LAL"],
            "key": {
                "L": {"item": "onter_ic2:lapotron_crystal"},
                "A": {"item": "onter_ic2:advanced_alloy"},
                "H": {"item": "onter_ic2:hybrid_solar_panel"},
                "S": {"item": "onter_ic2:sunnarium"}
            },
            "result": {"id": "onter_ic2:ultimate_hybrid_solar_panel", "count": 1}
        }
    },
    # Quantum Solar Panel
    {
        "file": "quantum_solar_panel.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["RSR", "QUQ", "RSR"],
            "key": {
                "R": {"item": "onter_ic2:reinforced_iridium_plate"},
                "S": {"item": "onter_ic2:sunnarium_plate"},
                "Q": {"item": "minecraft:nether_star"},
                "U": {"item": "onter_ic2:ultimate_hybrid_solar_panel"}
            },
            "result": {"id": "onter_ic2:quantum_solar_panel", "count": 1}
        }
    },
    # Superconductor Cable
    {
        "file": "superconductor_cable.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["RRR", "GSG", "RRR"],
            "key": {
                "R": {"item": "onter_ic2:reinforced_iridium_plate"},
                "G": {"item": "onter_ic2:glass_fibre_cable"},
                "S": {"item": "minecraft:nether_star"}
            },
            "result": {"id": "onter_ic2:superconductor_cable", "count": 4}
        }
    },
    # Overclocker Upgrade
    {
        "file": "overclocker_upgrade.json",
        "data": {
            "type": "minecraft:crafting_shaped",
            "pattern": ["CCC", "ECE", "CCC"],
            "key": {
                "C": {"item": "onter_ic2:copper_cable"},
                "E": {"item": "onter_ic2:electronic_circuit"}
            },
            "result": {"id": "onter_ic2:overclocker_upgrade", "count": 1}
        }
    }
]

# Machine Custom Processing Recipes
machine_recipes = [
    # Macerator
    {"file": "macerating_raw_iron.json", "type": "onter_ic2:macerating", "ing": {"item": "minecraft:raw_iron"}, "res": {"id": "alltheores:iron_dust", "fallback": {"id": "minecraft:iron_nugget", "count": 18}}, "energy": 800, "time": 200},
    {"file": "macerating_raw_copper.json", "type": "onter_ic2:macerating", "ing": {"item": "minecraft:raw_copper"}, "res": {"id": "alltheores:copper_dust", "fallback": {"id": "minecraft:copper_ingot", "count": 2}}, "energy": 800, "time": 200},
    {"file": "macerating_raw_gold.json", "type": "onter_ic2:macerating", "ing": {"item": "minecraft:raw_gold"}, "res": {"id": "alltheores:gold_dust", "fallback": {"id": "minecraft:gold_nugget", "count": 18}}, "energy": 800, "time": 200},
    {"file": "macerating_coal.json", "type": "onter_ic2:macerating", "ing": {"item": "minecraft:coal"}, "res": {"id": "alltheores:coal_dust", "fallback": {"id": "onter_ic2:carbon_fibre", "count": 1}}, "energy": 800, "time": 200},

    # Compressor
    {"file": "compressing_advanced_alloy.json", "type": "onter_ic2:compressing", "ing": {"item": "onter_ic2:mixed_metal_ingot"}, "res": {"id": "onter_ic2:advanced_alloy", "count": 1}, "energy": 1200, "time": 300},
    {"file": "compressing_carbon_plate.json", "type": "onter_ic2:compressing", "ing": {"item": "onter_ic2:carbon_mesh"}, "res": {"id": "onter_ic2:carbon_plate", "count": 1}, "energy": 1200, "time": 300},

    # Extractor
    {"file": "extracting_sticky_resin.json", "type": "onter_ic2:extracting", "ing": {"item": "onter_ic2:sticky_resin"}, "res": {"id": "onter_ic2:rubber", "count": 3}, "energy": 600, "time": 200},

    # Metal Former (Rolling)
    {"file": "rolling_iron_ingot.json", "type": "onter_ic2:metal_forming", "mode": "rolling", "ing": {"tag": "c:ingots/iron"}, "res": {"id": "alltheores:iron_plate", "fallback": {"id": "onter_ic2:advanced_alloy", "count": 1}}, "energy": 800, "time": 200},
    {"file": "rolling_copper_ingot.json", "type": "onter_ic2:metal_forming", "mode": "rolling", "ing": {"tag": "c:ingots/copper"}, "res": {"id": "alltheores:copper_plate", "fallback": {"id": "onter_ic2:copper_cable", "count": 2}}, "energy": 800, "time": 200},
    {"file": "rolling_gold_ingot.json", "type": "onter_ic2:metal_forming", "mode": "rolling", "ing": {"tag": "c:ingots/gold"}, "res": {"id": "alltheores:gold_plate", "fallback": {"id": "onter_ic2:gold_cable", "count": 2}}, "energy": 800, "time": 200},

    # Metal Former (Extruding)
    {"file": "extruding_copper_cable.json", "type": "onter_ic2:metal_forming", "mode": "extruding", "ing": {"tag": "c:ingots/copper"}, "res": {"id": "onter_ic2:copper_cable", "count": 3}, "energy": 800, "time": 200},
    {"file": "extruding_gold_cable.json", "type": "onter_ic2:metal_forming", "mode": "extruding", "ing": {"tag": "c:ingots/gold"}, "res": {"id": "onter_ic2:gold_cable", "count": 3}, "energy": 800, "time": 200},
]

for r in recipes:
    p = os.path.join(base_recipe_dir, r["file"])
    with open(p, "w") as f:
        json.dump(r["data"], f, indent=2)

for mr in machine_recipes:
    data = {
        "type": mr["type"],
        "ingredient": mr["ing"],
        "result": mr["res"],
        "energyCost": mr["energy"],
        "processTime": mr["time"]
    }
    if "mode" in mr:
        data["mode"] = mr["mode"]
    p = os.path.join(base_recipe_dir, mr["file"])
    with open(p, "w") as f:
        json.dump(data, f, indent=2)

print("Generated recipes!")
