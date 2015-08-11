# Animeflv 

Sirve:
-Obtencion de JSON de los capitulos recientes desde api oficial de Animeflv "http://animeflv.net/api.php?accion=inicio"
-Parser de JSON para sacar los titulos, tipo de capitulo, numero de capitulo, ID de anime, ID de capitulo <-- probablemente necesario para pedir descrga de capitulo desde servidor
-Contruccion de Link de las imagenes del anime usando el ID de anime y la direccion "http://cdn.animeflv.net/img/portada/thumb_80/" + ID de anime + .jpg
-Cargar imagenes desde web, y guardar su respectiva copia en cache (Usando libreria Picasso)
-Cargar Nombre y numero de capitulo de anime.
-(provicional) Construir link al sitio original, sacar link a la pagina de descarga respectiva de Zippyshare, e iniciar descarga desde un explorador alternativo.
-(provicional) en caso de llegar a un link de linkbucks en ves de zippyshare, simular un click al link de saltar publicidad usando javascript

Error:
-En la Activity de WebDescarga, no carga ningun link de animeflv, por lo q no pueden hacerse todos los comandos para decargar.

Falta:
-Obtencion de capitulos desde servidor de animeflv
-Menu lateral para todos los animes en el servidor.
-Poder ver los animes descargados haciendo click en Ver.
-Crear una UI para la informacion de los animes usando material design
-Diseñar parser para los codigos de directorio, informacion de anime.
-Guardar JSONs en SharedPreference o Base de datos para abrir app sin conexion.
-Metodo de obtencion de JSON basado en URLConection, en ves de cargar y obtener datos desde webview (carga de informacion mas rapida, y con menos errores)
