<?php
ini_set('display_errors', 1);
error_reporting(0);
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
        $Ddir = "../cache/dir.txt";
        $Tdir = "../cache/tdir.txt";
        $url = "http://animeflv.net/ajax/animes/lista_completa";
        $cont = @file_get_contents($url);
        if (strpos($cont, "var lanime") !== false) {
            $rep = str_replace("var lanime=", "", $cont);
            $trim = '{"cache":"0","lista":' . substr($rep, 0, -1) . "}";
            $wencode = json_decode($trim);
            $Sdir = file_get_contents($Ddir);
            $Tedir = file_get_contents($Tdir);
            if (isset($_GET['force']) or $Tedir !== $trim or isset($_GET['hide_url'])) {
                $obj = new stdClass();
                $array = array();
                $wlista = $wencode->lista;
                foreach ($wlista as $item) {
                    $dir = "../cache/aid/" . $item->a . ".txt";
                    $subjson = @file_get_contents($dir);
                    $url = 'http://api2.animeflvapp.xyz/getHtml.php?url=http://animeflv.net/' . strtolower($item->c) . '/' . $item->d . '.html&certificate=' . $_GET['certificate'];
                    if ($subjson === false) {
                        $subjson = @file_get_contents($url);
                    }
                    if ($subjson !== false) {
                        $json2 = json_decode($subjson);
                        $g = $json2->generos;
                        if ($g !== "") {
                            $item->e = $json2->generos;
                        } else {
                            $item->e = "Sin-Generos";
                        }
                    } else {
                        $item->e = "null";
                    }
                    if (!isset($_GET['hide_url'])) {
                        $item->f = $url;
                    }
                    array_push($array, $item);
                }
                $obj->cache = "0";
                $obj->lista = $array;
                header('Content-Type: application/json');
                echo json_encode($obj);
                if (!isset($_GET['hide_url'])) {
                    $final = json_encode($obj);
                    if (file_exists($Ddir)) {
                        $Sdir = file_get_contents($Ddir);
                        if ($Sdir !== $final) {
                            file_put_contents($Ddir, $final);
                        }
                    } else {
                        file_put_contents($Ddir, $final);
                    }
                    if (file_exists($Tdir)) {
                        $Tedir = file_get_contents($Tdir);
                        if ($Tedir !== $trim) {
                            file_put_contents($Tdir, $trim);
                        }
                    } else {
                        file_put_contents($Tdir, $trim);
                    }
                }
            } else {
                header('Content-Type: application/json');
                echo $Sdir;
            }
        } else {
            if (isset($_GET['bypass'])) {
                $cont = @file_get_contents("http://animeflvapp.pythonanywhere.com/v1/content?type=html&url=" . $url);
                if (strpos($cont, "var lanime") !== false) {
                    $rep = str_replace("var lanime=", "", $cont);
                    $trim = '{"cache":"0","lista":' . substr($rep, 0, -1) . "}";
                    $wencode = json_decode($trim);
                    $Sdir = file_get_contents($Ddir);
                    $Tedir = file_get_contents($Tdir);
                    if (isset($_GET['force']) or $Tedir !== $trim) {
                        $obj = new stdClass();
                        $array = array();
                        $wlista = $wencode->lista;
                        foreach ($wlista as $item) {
                            $dir = "../cache/aid/" . $item->a . ".txt";
                            $subjson = @file_get_contents($dir);
                            if ($subjson === false) {
                                $url = 'http://api2.animeflvapp.xyz/getHtml.php?url=http://animeflv.net/' . strtolower($item->c) . '/' . $item->d . '.html&certificate=' . $_GET['certificate'];
                                $subjson = @file_get_contents($url);
                            }
                            if ($subjson !== false) {
                                $json2 = json_decode($subjson);
                                $g = $json2->generos;
                                if ($g !== "") {
                                    $item->e = $json2->generos;
                                } else {
                                    $item->e = "Sin-Generos";
                                }
                            } else {
                                $item->e = "null";
                            }
                            array_push($array, $item);
                        }
                        $obj->lista = $array;
                        header('Content-Type: application/json');
                        echo json_encode($obj);
                        $final = json_encode($obj);
                        if (file_exists($Ddir)) {
                            $Sdir = file_get_contents($Ddir);
                            if ($Sdir !== $final) {
                                file_put_contents($Ddir, $final);
                            }
                        } else {
                            file_put_contents($Ddir, $final);
                        }
                        if (file_exists($Tdir)) {
                            $Tedir = file_get_contents($Tdir);
                            if ($Tedir !== $trim) {
                                file_put_contents($Tdir, $trim);
                            }
                        } else {
                            file_put_contents($Tdir, $trim);
                        }
                    } else {
                        header('Content-Type: application/json');
                        echo $Sdir;
                    }
                } else {
                    if (file_exists($Ddir)) {
                        $json = file_get_contents($Ddir);
                        $json = str_replace('"cache":"0"', '"cache":"1"', $json);
                        echo $json;
                    } else {
                        echo "error";
                    }
                }
            } else {
                $Ddir = "../cache/dir.txt";
                if (file_exists($Ddir)) {
                    $json = file_get_contents($Ddir);
                    $json = str_replace('"cache":"0"', '"cache":"1"', $json);
                    echo $json;

                }
            }
        }
    } else {
        header('HTTP/1.0 403 Forbidden');
        echo "No estas autorizado para usar esta api";
    }
} else {
    header('HTTP/1.0 403 Forbidden');
    echo "Falta el certificado";
}
?>