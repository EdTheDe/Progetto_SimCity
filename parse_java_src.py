import os
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

java_files = []
for root, dirs, files in os.walk('src/main/java'):
    for file in files:
        if file.endswith('.java'):
            java_files.append(os.path.join(root, file))

output = []
count = 1

# A helper function to parse a java file content and extract classes/interfaces
def parse_java_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove block comments
    content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    # Remove line comments
    content = re.sub(r'//.*', '', content)
    
    # Extract entities
    entities = {}
    
    # regex to find classes, abstract classes, interfaces, records
    # match modifiers, type (class/interface), name, and opening brace
    # also handle extends/implements
    entity_regex = re.compile(r'((?:public|private|protected|abstract|static|final|\s)*)\s+(class|interface)\s+([A-Za-z0-9_]+)[^{]*\{')
    
    for match in entity_regex.finditer(content):
        mod = match.group(1).strip()
        typ = match.group(2).strip()
        name = match.group(3).strip()
        
        if name not in classes_to_find:
            continue
            
        ent_type = "Class"
        if typ == "interface":
            ent_type = "Interface"
        elif "abstract" in mod:
            ent_type = "Abstract Class"
        elif "Exception" in name:
            ent_type = "Exception"
            
        start_idx = match.end()
        # Find matching brace
        brace_count = 1
        end_idx = start_idx
        for i in range(start_idx, len(content)):
            if content[i] == '{':
                brace_count += 1
            elif content[i] == '}':
                brace_count -= 1
            if brace_count == 0:
                end_idx = i
                break
                
        body = content[start_idx:end_idx]
        entities[name] = {
            "type": ent_type,
            "body": body,
            "name": name
        }
    return entities

all_entities = {}
for jf in java_files:
    entities = parse_java_file(jf)
    all_entities.update(entities)

# Now extract fields and methods from body
def extract_members(body, class_name):
    # Regex for fields and methods
    # It's tricky to parse Java with regex perfectly, so we'll do line by line heuristic
    fields = []
    methods = []
    
    # Clean string literals to avoid semicolon/brace confusion
    body = re.sub(r'"(?:\\.|[^"\\])*"', '""', body)
    
    # Split by statements/blocks
    # Actually, we can look for method signatures:
    # modifiers return_type name(params) { ... } or throws ...
    # And fields: modifiers type name = ... ;
    
    # Let's clean out method bodies
    # Whenever we see '{', we find matching '}' and remove the whole block, replacing it with a placeholder or just semicolon.
    clean_body = ""
    i = 0
    while i < len(body):
        if body[i] == '{':
            clean_body += ";"
            bc = 1
            i += 1
            while i < len(body) and bc > 0:
                if body[i] == '{': bc += 1
                elif body[i] == '}': bc -= 1
                i += 1
        else:
            clean_body += body[i]
            i += 1
            
    # Now clean_body only contains field declarations and method signatures ending with ';'
    statements = [s.strip() for s in clean_body.split(';') if s.strip()]
    
    for stmt in statements:
        if stmt.startswith('@'): continue # skip annotations
        # remove annotations inside statement
        stmt = re.sub(r'@\w+(\([^)]*\))?\s*', '', stmt)
        
        # Check if it's a method (contains '(' and ')')
        if '(' in stmt and ')' in stmt and not '=' in stmt.split('(')[0]:
            # Method or Constructor
            # Remove throws
            sig = stmt.split('throws')[0].strip()
            
            # Format: modifiers return_type name(params)
            # Find the parameter block
            param_start = sig.find('(')
            param_end = sig.rfind(')')
            params = sig[param_start:param_end+1]
            prefix = sig[:param_start].strip()
            
            # extract modifiers, return type, name
            parts = prefix.split()
            if len(parts) == 0: continue
            
            if parts[-1] == class_name:
                # constructor
                mods = " ".join(parts[:-1]) if len(parts) > 1 else ""
                if not mods: mods = "package-private"
                methods.append(f"{mods} {class_name}{params}")
            else:
                # method
                name = parts[-1]
                ret_type = parts[-2] if len(parts) >= 2 else "void"
                mods = " ".join(parts[:-2]) if len(parts) > 2 else ""
                if not mods: mods = "package-private"
                methods.append(f"{mods} {ret_type} {name}{params}")
        else:
            # Field
            # could be "private int x = 5"
            # remove assignment
            stmt = stmt.split('=')[0].strip()
            if not stmt: continue
            if stmt == "return" or stmt == "break" or stmt == "continue": continue
            # format: modifiers type name
            parts = stmt.split()
            if len(parts) < 2: continue
            name = parts[-1]
            typ = parts[-2]
            mods = " ".join(parts[:-2]) if len(parts) > 2 else ""
            if not mods: mods = "package-private"
            fields.append(f"{mods} {typ} {name}")
            
    return fields, methods

for class_name in sorted(classes_to_find):
    if class_name not in all_entities:
        output.append(f"{count} - {class_name} (Class non trovata)")
        count += 1
        continue
        
    ent = all_entities[class_name]
    output.append(f"{count} - {class_name} ({ent['type']})")
    
    fields, methods = extract_members(ent['body'], class_name)
    
    output.append("   - Variabili d'istanza:")
    if fields:
        for f in fields: output.append("      " + f)
    else:
        output.append("      (Nessuna variabile d'istanza)")
        
    output.append("   - Metodi:")
    if methods:
        for m in methods: output.append("      " + m)
    else:
        output.append("      (Nessun metodo)")
        
    output.append("")
    count += 1

with open('javap_parsed_with_names.txt', 'w', encoding='utf-8') as f:
    f.write('\n'.join(output))

