<?php
$filename = './feedback.txt';
if (isset($_GET["tipo"])){
        $tipo=$_GET['tipo'];
    }
if (isset($_GET["cuenta"])){
        $cuenta=$_GET['cuenta'];
    }
if (isset($_GET["nombre"])){
        $nombre=$_GET['nombre'];
    }
if (isset($_GET["data"])){
        $data=$_GET['data'];
    }
if (isset($_GET["nombre"]) and isset($_GET["data"]) and isset($_GET["tipo"]) and isset($_GET["cuenta"])){
$Content=$nombre.':::'.$tipo.':::'.$cuenta.':::'.$data.'&&&'."\n"."\n";
if(!file_put_contents($filename, $Content, FILE_APPEND)){
    echo 'Error';
}
else{
    echo 'OK';
}
}
else{
    ob_end_clean();
    echo 'Sin acciones';
}
if (isset($_GET["ok"])){
    ob_end_clean();
    echo 'OK';echo 'OK';
}
    ?>