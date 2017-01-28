<?php
    ini_set('display_errors',1); error_reporting(E_ALL);
$seted=isset($_GET['certificate']);
if ($seted){ 
    $cert = $_GET['certificate'];
    $usuario="u620539434_admin";
    $contra="grim042a677";
    $DataBase="u620539434_certs";
    $db = new mysqli('mysql.hostinger.mx', $usuario, $contra, $DataBase);
    $sql = <<<SQL
    SELECT id
    FROM certificados
    WHERE certificado = '{$cert}'
SQL;
    $resultado = $db->query($sql);
if ($resultado->num_rows != 0  or file_get_contents("../open_sql.txt")=="true"){
    if (isset($_GET['thumb'])){
        $dir="../cache/thumb/";
        $url = $_GET['thumb'];
        $content=get_headers($url);
        if(strpos($content[0],"200")!==false){
            $content=file_get_contents($url);
            header('Content-type: image/jpeg');
            echo $content;
            $nombre=substr($url,strrpos($url,"/")+1);
            if (!file_exists($dir.$nombre)){
                file_put_contents($dir.$nombre,$content);
            }
        }
        else{
            $nombre=substr($url,strrpos($url,"/")+1);
            if (file_exists($dir.$nombre)){
                header('Content-type: image/jpeg');
                echo file_get_contents($dir.$nombre);
            }
            else{
                echo "Sin Cache";
            }
        }
    }
if (isset($_GET['portada'])){
        $dir="../cache/portada/";
        $url = $_GET['portada'];
        $content=get_headers($url);
        if(strpos($content[0],"200")!==false){
            $content=file_get_contents($url);
            header('Content-type: image/jpeg');
            echo $content;
            $nombre=substr($url,strrpos($url,"/")+1);
            file_put_contents($dir.$nombre,$content);
        }
        else{
            $nombre=substr($url,strrpos($url,"/")+1);
            if (file_exists($dir.$nombre)){
                header('Content-type: image/jpeg');
                echo file_get_contents($dir.$nombre);
            }
            else{
                echo "Sin Cache";
            }
        }
    }
    if(isset($_GET['aid'])){
        $nombre=$_GET['aid'].'.jpg';
        $type=$_GET['type'];
        if ($type==='portada'){
            $dir="../cache/portada/";
            if (file_exists($dir.$nombre)){
                header('Content-type: image/jpeg');
                echo file_get_contents($dir.$nombre);
            }
            else{
                echo "Sin Cache";
            }
        }
        if ($type==='thumb'){
            $dir="../cache/thumb/";
            if (file_exists($dir.$nombre)){
                header('Content-type: image/jpeg');
                echo file_get_contents($dir.$nombre);
            }
            else{
                echo "Sin Cache";
            }
        }
    }
    if (isset($_GET['hd'])){
        $dir="../cache/portada/";
        $dirhd="../cache/hd/";
        $url = $_GET['hd'];
        $nombre=substr($url,strrpos($url,"/")+1);
        if (file_exists($dirhd.$nombre)){
            header('Content-type: image/jpeg');
            echo file_get_contents($dirhd.$nombre);
        }else{
            $content=get_headers($url);
        if(strpos($content[0],"200")!==false){
            $content=file_get_contents($url);
            header('Content-type: image/jpeg');
            echo $content;
            $nombre=substr($url,strrpos($url,"/")+1);
            file_put_contents($dir.$nombre,$content);
        }
        else{
            $nombre=substr($url,strrpos($url,"/")+1);
            if (file_exists($dir.$nombre)){
                header('Content-type: image/jpeg');
                echo file_get_contents($dir.$nombre);
            }
            else{
                echo "Sin Cache";
            }
        }
        }
    }
    }
    else{
        header('HTTP/1.0 403 Forbidden');
        echo "No estas autorizado para usar esta api";
    }
}
else{
    header('HTTP/1.0 403 Forbidden');
    echo "Falta el certificado";
}
?>