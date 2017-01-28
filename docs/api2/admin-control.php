<?php
ini_set('display_errors', 1);
error_reporting(0);
$use_scraper = false;
$bypasser = "http://appapi.seriesgt.com/v1/content?type=html&url=";
$seted = isset($_GET['certificate']);
if ($seted) {
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
        if (isset($_GET['g_pass'])) {
            if (isset($_GET['email_coded'])) {
                $file = '../user_favs/' . $_GET['email_coded'] . ".txt";
                if (file_exists($file)) {
                    header('HTTP/1.1 200 OK');
                    header('Content-Type: application/json');
                    $obj = new stdClass();
                    $obj->isCoded = true;
                    $obj->response = json_decode(file_get_contents($file))->cont;
                    echo json_encode($obj);
                } else {
                    header('HTTP/1.1 404 Not Found');
                    echo "Usuario no encontrado";
                }
            } else {
                header('HTTP/1.1 400 Bad Request');
                echo "Falta el correo";
            }
        } else if (isset($_GET['f_c_email'])) {
            if (isset($_GET['email_coded']) and isset($_GET['new_email_coded'])) {
                $file = '../user_favs/' . $_GET['email_coded'] . ".txt";
                $nfile = '../user_favs/' . $_GET['new_email_coded'] . ".txt";
                if (file_exists($file)) {
                    header('HTTP/1.1 200 OK');
                    header('Content-Type: application/json');
                    if (!file_exists($nfile)) {
                        if (file_put_contents($nfile, json_encode(file_get_contents($file))) != false) {
                            unlink($file);
                            $response = "Email cambiado";
                        } else {
                            $response = "Error al cambiar";
                        }
                    } else {
                        $response = "Email ya existe";
                    }
                    $obj = new stdClass();
                    $obj->isCoded = false;
                    $obj->response = $response;
                    echo json_encode($obj);
                } else {
                    header('HTTP/1.1 404 Not Found');
                    echo "Usuario no encontrado";
                }
            } else {
                header('HTTP/1.1 400 Bad Request');
                echo "Falta el correo";
            }
        } else if (isset($_GET['f_c_pass'])) {
            if (isset($_GET['email_coded']) and isset($_GET['new_pass_coded'])) {
                $file = '../user_favs/' . $_GET['email_coded'] . ".txt";
                if (file_exists($file)) {
                    header('HTTP/1.1 200 OK');
                    header('Content-Type: application/json');
                    $n_obj = json_decode(file_get_contents($file));
                    $n_obj->cont = $_GET['new_pass_coded'];
                    if (file_put_contents($file, json_encode($n_obj))) {
                        $response = "Contraseña Cambiada";
                    } else {
                        $response = "Error al cambiar";
                    }
                    $obj = new stdClass();
                    $obj->isCoded = false;
                    $obj->response = $response;
                    echo json_encode($obj);
                } else {
                    header('HTTP/1.1 404 Not Found');
                    echo "Usuario no encontrado";
                }
            } else {
                header('HTTP/1.1 400 Bad Request');
                echo "Falta el correo";
            }

        } else if (isset($_GET['delete'])) {
            if (isset($_GET['email_coded'])) {
                $file = '../user_favs/' . $_GET['email_coded'] . ".txt";
                if (file_exists($file)) {
                    header('HTTP/1.1 200 OK');
                    header('Content-Type: application/json');
                    unlink($file);
                    $obj = new stdClass();
                    $obj->isCoded = false;
                    $obj->response = "Cuenta eliminada";
                    echo json_encode($obj);
                    deleteEmail();
                } else {
                    header('HTTP/1.1 404 Not Found');
                    echo "Usuario no encontrado";
                }
            } else {
                header('HTTP/1.1 400 Bad Request');
                echo "Falta el correo";
            }

        } else {
            header('HTTP/1.1 403 Forbidden');
            echo "Faltan Parametros";
        }
    } else {
        header('HTTP/1.1 403 Forbidden');
        echo "No estas autorizado para usar esta api";
    }
} else {
    header('HTTP/1.1 403 Forbidden');
    echo "Error en certificado";
}

function deleteEmail()
{
    $listFile = "../user_favs/list.json";
    if (isset($_GET["email_normal"])) {
        $email = $_GET["email_normal"];
        if (file_exists($listFile)) {
            $json = json_decode(file_get_contents($listFile));
            $list = $json->list;
            if (in_array($email, $list)) {
                $index = array_search($email, $list);
                if ($index !== FALSE) {
                    unset($list[$index]);
                    $json->list = array_values($list);
                    file_put_contents($listFile, json_encode($json,JSON_PRETTY_PRINT));
                }
            }
        }
    }
}

?>