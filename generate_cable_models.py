import os
import json

base_res = r"E:\minecraft\myMods\onter_ic2\src\main\resources\assets\onter_ic2"

cables = [
    "copper_cable",
    "gold_cable",
    "hv_cable",
    "glass_fibre_cable",
    "superconductor_cable"
]

models_block_dir = os.path.join(base_res, "models", "block")
models_item_dir = os.path.join(base_res, "models", "item")
blockstates_dir = os.path.join(base_res, "blockstates")

os.makedirs(models_block_dir, exist_ok=True)
os.makedirs(models_item_dir, exist_ok=True)
os.makedirs(blockstates_dir, exist_ok=True)

for cable in cables:
    # 1. Core Model
    core_model = {
        "parent": "minecraft:block/block",
        "textures": {
            "particle": f"onter_ic2:block/{cable}",
            "cable": f"onter_ic2:block/{cable}"
        },
        "elements": [
            {
                "from": [5, 5, 5],
                "to": [11, 11, 11],
                "faces": {
                    "down":  {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "down"},
                    "up":    {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "up"},
                    "north": {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "north"},
                    "south": {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "south"},
                    "west":  {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "west"},
                    "east":  {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "east"}
                }
            }
        ]
    }
    with open(os.path.join(models_block_dir, f"{cable}_core.json"), "w") as f:
        json.dump(core_model, f, indent=2)

    # 2. Side Model (pointing North / Z=0..5)
    side_model = {
        "parent": "minecraft:block/block",
        "textures": {
            "particle": f"onter_ic2:block/{cable}",
            "cable": f"onter_ic2:block/{cable}"
        },
        "elements": [
            {
                "from": [5, 5, 0],
                "to": [11, 11, 5],
                "faces": {
                    "down":  {"uv": [5, 0, 11, 5], "texture": "#cable"},
                    "up":    {"uv": [5, 0, 11, 5], "texture": "#cable"},
                    "north": {"uv": [5, 5, 11, 11], "texture": "#cable", "cullface": "north"},
                    "south": {"uv": [5, 5, 11, 11], "texture": "#cable"},
                    "west":  {"uv": [0, 5, 5, 11], "texture": "#cable"},
                    "east":  {"uv": [11, 5, 16, 11], "texture": "#cable"}
                }
            }
        ]
    }
    with open(os.path.join(models_block_dir, f"{cable}_side.json"), "w") as f:
        json.dump(side_model, f, indent=2)

    # 3. Multipart Blockstate
    blockstate = {
        "multipart": [
            {
                "apply": {"model": f"onter_ic2:block/{cable}_core"}
            },
            {
                "when": {"north": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side"}
            },
            {
                "when": {"south": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side", "y": 180}
            },
            {
                "when": {"west": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side", "y": 270}
            },
            {
                "when": {"east": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side", "y": 90}
            },
            {
                "when": {"up": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side", "x": 270}
            },
            {
                "when": {"down": "true"},
                "apply": {"model": f"onter_ic2:block/{cable}_side", "x": 90}
            }
        ]
    }
    with open(os.path.join(blockstates_dir, f"{cable}.json"), "w") as f:
        json.dump(blockstate, f, indent=2)

    # 4. Item Model
    item_model = {
        "parent": f"onter_ic2:block/{cable}_core"
    }
    with open(os.path.join(models_item_dir, f"{cable}.json"), "w") as f:
        json.dump(item_model, f, indent=2)

    print(f"Generated multipart cable models for {cable}")

print("All cable models generated!")
