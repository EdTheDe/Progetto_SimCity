import subprocess
import os
import glob
import re

classes_to_find = [
    "AssetManager", "BuilderValidator", "Building", "Cell", "CityApp", "CityObserver", "Commercial",
    "CostruzioneException", "CrisiEconomicaEvent", "FireStation", "GestoreEventiUI", "GreenArea",
    "GuerraEvent", "Hospital", "Industrial", "Infrastructure", "MappaGriglia", "MenuImpostazioni",
    "PannelloControlliTempo", "PannelloNotifiche", "PannelloRisorse", "PersistenceManager",
    "PioggiaDiMeteoritiEvent", "PoliceStation", "PoliticaAmbientale", "PoliticaIndustriale",
    "PoliticaNeutrale", "PoliticaStrategy", "PowerPlant", "PrimaveraEvent", "RandomEvent",
    "RegolaCollegamentoServizi", "RegolaCostruzione", "RegolaFondiSufficienti", "RegolaSpazioLibero",
    "Residential", "Road", "SavedEntityData", "SaveGameData", "School", "SideBar",
    "SimulationEngine", "StateBuilding", "StatoCitta", "TickStats", "TimeBar", "TopBar",
    "TutorialPopup", "UrbanEntity", "UrbanEntityFactory", "UrbanGrid", "WaterPlant"
]

class_files = []
for root, dirs, files in os.walk('target/classes'):
    for file in files:
        if file.endswith('.class') and '$' not in file:
            class_files.append(os.path.join(root, file))
        elif file.endswith('.class') and 'BuilderValidator$' in file:
             class_files.append(os.path.join(root, file))

# Some classes like RegolaSpazioLibero are compiled as citylogic/core/validation/RegolaSpazioLibero.class
output = []
count = 1

for class_name in sorted(classes_to_find):
    # Find corresponding .class file
    target_cf = None
    for cf in class_files:
        if cf.endswith(f"/{class_name}.class"):
            target_cf = cf
            break
        if cf.endswith(f"${class_name}.class"):
            target_cf = cf
            break
            
    if not target_cf:
        output.append(f"{count} - {class_name} (Class non trovata nei .class)")
        count += 1
        continue
        
    javap_out = subprocess.check_output(['javap', '-p', target_cf]).decode('utf-8')
    lines = [l.strip() for l in javap_out.split('\n') if l.strip()]
    
    # First line is something like "Compiled from..."
    # Second line is the class declaration
    decl = ""
    for l in lines:
        if "class " in l or "interface " in l:
            decl = l.split('{')[0].strip()
            break
            
    tipo = "Class"
    if "abstract class" in decl:
        tipo = "Abstract Class"
    elif "interface" in decl:
        tipo = "Interface"
    if "Exception" in decl:
        tipo = "Exception"
        
    output.append(f"{count} - {class_name} ({tipo})")
    
    # variables and methods
    vars_list = []
    methods_list = []
    
    for l in lines:
        if "Compiled from" in l or l.startswith("class ") or l.startswith("public class ") or l.startswith("abstract ") or l.startswith("interface ") or l.startswith("public interface") or l == "}" or l.startswith("public abstract class") or l.startswith("class "):
            # ignore
            pass
        else:
            # check if method or variable
            if "(" in l and ")" in l:
                # it's a method
                m = l.rstrip(';')
                methods_list.append("      " + m)
            else:
                # it's a variable
                v = l.rstrip(';')
                if v and "static" not in v: # remove static constants if any, or keep them? keep them.
                    vars_list.append("      " + v)
                    
    output.append("   - Variabili d'istanza:")
    if vars_list:
        for v in vars_list: output.append(v)
    else:
        output.append("      (Nessuna variabile d'istanza)")
        
    output.append("   - Metodi:")
    if methods_list:
        for m in methods_list: output.append(m)
    else:
        output.append("      (Nessun metodo)")
        
    output.append("")
    count += 1

with open('javap_parsed.txt', 'w') as f:
    f.write('\n'.join(output))
