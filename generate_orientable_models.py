import os
import json

base_dir = r"e:\minecraft\myMods\onter_ic2\src\main\resources\assets\onter_ic2"

machines = ['macerator', 'electric_furnace', 'compressor', 'extractor', 'metal_former', 'generator']
storages = ['batbox', 'cesu', 'mfe', 'mfsu']
solars = ['solar_panel', 'advanced_solar_panel', 'hybrid_solar_panel', 'ultimate_hybrid_solar_panel', 'quantum_solar_panel']

# 1. Machines Orientable Models & Blockstates
for m in machines:
    # Inactive Model
    model_inactive = {
        "parent": "minecraft:block/orientable_with_bottom",
        "textures": {
            "top": f"onter_ic2:block/{m}_top",
            "bottom": f"onter_ic2:block/{m}_bottom",
            "side": f"onter_ic2:block/{m}_side",
            "front": f"onter_ic2:block/{m}_front"
        }
    }
    with open(os.path.join(base_dir, "models", "block", f"{m}.json"), "w") as f:
        json.dump(model_inactive, f, indent=2)

    # Active Model
    model_active = {
        "parent": "minecraft:block/orientable_with_bottom",
        "textures": {
            "top": f"onter_ic2:block/{m}_top",
            "bottom": f"onter_ic2:block/{m}_bottom",
            "side": f"onter_ic2:block/{m}_side",
            "front": f"onter_ic2:block/{m}_front_active"
        }
    }
    with open(os.path.join(base_dir, "models", "block", f"{m}_active.json"), "w") as f:
        json.dump(model_active, f, indent=2)

    # Blockstate
    bs = {
        "variants": {
            "facing=north,lit=false": {"model": f"onter_ic2:block/{m}"},
            "facing=south,lit=false": {"model": f"onter_ic2:block/{m}", "y": 180},
            "facing=west,lit=false": {"model": f"onter_ic2:block/{m}", "y": 270},
            "facing=east,lit=false": {"model": f"onter_ic2:block/{m}", "y": 90},
            "facing=north,lit=true": {"model": f"onter_ic2:block/{m}_active"},
            "facing=south,lit=true": {"model": f"onter_ic2:block/{m}_active", "y": 180},
            "facing=west,lit=true": {"model": f"onter_ic2:block/{m}_active", "y": 270},
            "facing=east,lit=true": {"model": f"onter_ic2:block/{m}_active", "y": 90}
        }
    }
    with open(os.path.join(base_dir, "blockstates", f"{m}.json"), "w") as f:
        json.dump(bs, f, indent=2)

# 2. Storages
for s in storages:
    model_s = {
        "parent": "minecraft:block/orientable_with_bottom",
        "textures": {
            "top": f"onter_ic2:block/{s}_top",
            "bottom": f"onter_ic2:block/{s}_bottom",
            "side": f"onter_ic2:block/{s}_side",
            "front": f"onter_ic2:block/{s}_front"
        }
    }
    with open(os.path.join(base_dir, "models", "block", f"{s}.json"), "w") as f:
        json.dump(model_s, f, indent=2)

    bs = {
        "variants": {
            "": {"model": f"onter_ic2:block/{s}"}
        }
    }
    with open(os.path.join(base_dir, "blockstates", f"{s}.json"), "w") as f:
        json.dump(bs, f, indent=2)

# 3. Solars
for sol in solars:
    bot_tex = f"onter_ic2:block/{sol}_bottom" if os.path.exists(os.path.join(base_dir, "textures", "block", f"{sol}_bottom.png")) else "onter_ic2:block/machine_casing"
    model_sol = {
        "parent": "minecraft:block/cube_bottom_top",
        "textures": {
            "top": f"onter_ic2:block/{sol}_top",
            "bottom": bot_tex,
            "side": f"onter_ic2:block/{sol}_side"
        }
    }
    with open(os.path.join(base_dir, "models", "block", f"{sol}.json"), "w") as f:
        json.dump(model_sol, f, indent=2)

    bs = {
        "variants": {
            "": {"model": f"onter_ic2:block/{sol}"}
        }
    }
    with open(os.path.join(base_dir, "blockstates", f"{sol}.json"), "w") as f:
        json.dump(bs, f, indent=2)

print("Generated orientable block models and blockstates!")
