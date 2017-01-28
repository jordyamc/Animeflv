<?php
ini_set('display_errors', 1);
error_reporting(0);
if (isset($_GET['certificate'])) {
    $cert = $_GET['certificate'];
    $usuario = "u620539434_admin";
    $contra = "grim042a677";
    $DataBase = "u620539434_certs";
    $db = new mysqli('mysql.hostinger.mx', $usuario, $contra, $DataBase);
    $sql = <<<SQL
    SELECT id
    FROM certificados
    WHERE certificado = '{$cert}'
SQL;
    $resultado = $db->query($sql);
    if ($resultado->num_rows != 0 or file_get_contents("../open_sql.txt") == "true") {
        header('Content-Type: application/json');
        if (isset($_GET["get"])){
            if (isset($_GET["aid"])){
                $file="../times/list/".$_GET["aid"].".data";
                if (file_exists($file)){
                    $json=json_decode(file_get_contents($file));
                    $json->response="ok";
                    $json->exist=true;
                    echo json_encode($json);
                }else{
                    $json=new stdClass();
                    $json->response="ok";
                    $json->exist=false;
                    echo json_encode($json);
                }
            }else{
                $json=new stdClass();
                $json->response="error";
                echo json_encode($json);
            }
        }else if (isset($_GET["edit"])){
            if (isset($_GET["aid"]) and isset($_GET["delete"])){
                $file="../times/list/".$_GET["aid"].".data";
                if (file_exists($file)){
                    unlink($file);
                }
                $json=new stdClass();
                $json->response="ok";
                $json->exist=false;
                echo json_encode($json);
            }else if (isset($_GET["aid"]) and isset($_GET["title"]) and isset($_GET["daycode"]) and isset($_GET["hour"])){
                $file="../times/list/".$_GET["aid"].".data";
                if (file_exists($file)){
                    $json=json_decode(file_get_contents($file));
                    $json->daycode=strval($_GET["daycode"]);
                    $json->hour=$_GET["hour"];
                    file_put_contents($file,json_encode($json,JSON_PRETTY_PRINT));
                    $json->response="ok";
                    $json->exist=true;
                    echo json_encode($json);
                }else{
                    $json=new stdClass();
                    $json->aid=$_GET["aid"];
                    $json->titulo=urldecode($_GET["title"]);
                    $json->daycode=strval($_GET["daycode"]);
                    $json->hour=$_GET["hour"];
                    file_put_contents($file,json_encode($json,JSON_PRETTY_PRINT));
                    $json->response="ok";
                    $json->exist=true;
                    echo json_encode($json);
                }
            }else {
                $json=new stdClass();
                $json->response="error";
                echo json_encode($json);
            }
        }else {
            $json=new stdClass();
            $json->response="error";
            echo json_encode($json);
        }
    }
}