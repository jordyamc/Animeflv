<?php
ini_set('display_errors', 1);
error_reporting(0);
$n_cuenta = 'nCuenta';
$get_fav = 'get';
$c_correo = 'cCuenta';
$c_pass = 'cPass';
$c_Account = 'cAccount';
$ref = 'refresh';
$list = 'list';
$addfav = 'addfav';
$delfav = 'delfav';
$setvistos = 'setvistos';
$addUser='addU';
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
        if (isset($_GET["tipo"])) {
            $tipo = $_GET['tipo'];
            if ($tipo == $n_cuenta) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["fav_code"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $favs = $_GET['fav_code'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (!file_exists($file)) {
                        list($favoritos, $vistos) = explode(":;:", $favs);
                        $obj = new stdClass();
                        $obj->cont = $pass;
                        $obj->favs = $favoritos;
                        $obj->vistos = $vistos;
                        $json = json_encode($obj);
                        if (file_put_contents($file, $json) != false) {
                            $response = new stdClass();
                            $response->response = "ok";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                            saveEmail();
                        } else {
                            $response = new stdClass();
                            $response->response = "server-error";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $get_fav) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        $obj->response = "ok";
                        $cont = $obj->cont;
                        if ($cont == $pass) {
                            header('Content-Type: application/json');
                            echo json_encode($obj);
                            saveEmail();
                        } else {
                            $response = new stdClass();
                            $response->response = "password";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "no-exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $ref) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["new_favs"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $nfavs = $_GET['new_favs'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        //list($cont,$favs)=explode("&&&",$raw);
                        $cont = $obj->cont;
                        if ($cont == $pass) {
                            list($favoritos, $vistos) = explode(":;:", $nfavs);
                            $obj = new stdClass();
                            $obj->cont = $pass;
                            $obj->favs = $favoritos;
                            $obj->vistos = $vistos;
                            $json = json_encode($obj);
                            if (file_put_contents($file, $json) != false) {
                                $nraw = file_get_contents($file);
                                $json = json_decode($nraw);
                                $json->response = "ok";
                                header('Content-Type: application/json');
                                echo json_encode($json);
                                saveEmail();
                            } else {
                                $response = new stdClass();
                                $response->response = "server-error";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                            }
                        } else {
                            $response = new stdClass();
                            $response->response = "password";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "no-exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $c_correo) {
                if (isset($_GET["past_email_coded"]) and isset($_GET["new_email_coded"]) and isset($_GET["pass_coded"])) {
                    $email = $_GET['past_email_coded'];
                    $nemail = $_GET['new_email_coded'];
                    $pass = $_GET['pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    $nfile = '../user_favs/' . $nemail . ".txt";
                    if (!file_exists($nfile)) {
                        $raw = file_get_contents($file);
                        //list($cont,$favs)=explode("&&&",$raw);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        if ($cont == $pass) {
                            if (file_put_contents($nfile, $raw) != false) {
                                unlink($file);
                                $response = new stdClass();
                                $response->response = "ok";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                                replaceEmail();
                            } else {
                                $response = new stdClass();
                                $response->response = "server-error";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                            }
                        } else {
                            $response = new stdClass();
                            $response->response = "password";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $c_Account) {
                if (isset($_GET["past_email_coded"]) and isset($_GET["new_email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["new_pass_coded"])) {
                    $email = $_GET['past_email_coded'];
                    $nemail = $_GET['new_email_coded'];
                    $pass = $_GET['pass_coded'];
                    $npass = $_GET['new_pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    $nfile = '../user_favs/' . $nemail . ".txt";
                    if (!file_exists($nfile)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        $obj->cont = $npass;
                        if ($cont == $pass) {
                            if (file_put_contents($nfile, $raw) != false) {
                                unlink($file);
                                $response = new stdClass();
                                $response->response = "ok";
                                saveEmail();
                                header('Content-Type: application/json');
                                echo json_encode($response);
                                replaceEmail();
                            } else {
                                $response = new stdClass();
                                $response->response = "server-error";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                            }
                        } else {
                            $response = new stdClass();
                            $response->response = "password";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $c_pass) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["new_pass_coded"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $npass = $_GET['new_pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        //list($cont,$favs)=explode("&&&",$raw);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        if ($cont == $pass) {
                            $nfile = '../user_favs/' . $nemail . ".txt";
                            list($favoritos, $vistos) = explode(":;:", $favs);
                            $obj = new stdClass();
                            $obj->cont = $npass;
                            $obj->favs = $favoritos;
                            $obj->vistos = $vistos;
                            $json = json_encode($obj);
                            if (file_put_contents($file, $json) != false) {
                                $response = new stdClass();
                                $response->response = "ok";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                                saveEmail();
                            } else {
                                $response = new stdClass();
                                $response->response = "server-error";
                                header('Content-Type: application/json');
                                echo json_encode($response);
                            }
                        } else {
                            $response = new stdClass();
                            $response->response = "password";
                            header('Content-Type: application/json');
                            echo json_encode($response);
                        }
                    } else {
                        $response = new stdClass();
                        $response->response = "no-exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                } else {
                    $response = new stdClass();
                    $response->response = "no-info";
                    header('Content-Type: application/json');
                    echo json_encode($response);
                }
            }

            if ($tipo == $addfav) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["aid"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        $vistos = $obj->vistos;
                        if ($cont == $pass) {
                            $favoritos = array();
                            if (strpos($obj->favs, ':::') !== false) {
                                $favoritos = explode(":::", $obj->favs);
                                $fav = array();
                                foreach ($favoritos as $item) {
                                    if ($item !== '') {
                                        array_push($fav, $item);
                                    }
                                }
                                $favoritos = $fav;
                            } else {
                                $favoritos = explode(",", $obj->favs);
                            }
                            if (!in_array($_GET['aid'], $favoritos)) {
                                array_push($favoritos, $_GET['aid']);
                            }
                            $obj = new stdClass();
                            $obj->cont = $pass;
                            $obj->favs = implode(",", $favoritos);
                            $obj->vistos = $vistos;
                            $json = json_encode($obj);
                            if (file_put_contents($file, $json) != false) {
                                $nraw = file_get_contents($file);
                                echo $nraw;
                            } else {
                                echo "Error";
                            }
                        } else {
                            echo "Contraseña";
                        }
                    } else {
                        echo "No Existe";
                    }
                } else {
                    echo "Falta informacion";
                }
            }

            if ($tipo == $delfav) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["aid"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        $vistos = $obj->vistos;
                        if ($cont == $pass) {
                            $favoritos = array();
                            if (strpos($obj->favs, ':::') !== false) {
                                $favoritos = explode(":::", $obj->favs);
                                $fav = array();
                                foreach ($favoritos as $item) {
                                    if ($item !== '') {
                                        array_push($fav, $item);
                                    }
                                }
                                $favoritos = $fav;
                            } else {
                                $favoritos = explode(",", $obj->favs);
                            }
                            if (in_array($_GET['aid'], $favoritos)) {
                                array_diff($favoritos, $_GET['aid']);
                            }
                            $obj = new stdClass();
                            $obj->cont = $pass;
                            $obj->favs = implode(",", $favoritos);
                            $obj->vistos = $vistos;
                            $json = json_encode($obj);
                            if (file_put_contents($file, $json) != false) {
                                $nraw = file_get_contents($file);
                                echo $nraw;
                            } else {
                                echo "Error";
                            }
                        } else {
                            echo "Contraseña";
                        }
                    } else {
                        echo "No Existe";
                    }
                } else {
                    echo "Falta informacion";
                }
            }

            if ($tipo == $setvistos) {
                if (isset($_GET["email_coded"]) and isset($_GET["pass_coded"]) and isset($_GET["code"])) {
                    $email = $_GET['email_coded'];
                    $pass = $_GET['pass_coded'];
                    $file = '../user_favs/' . $email . ".txt";
                    if (file_exists($file)) {
                        $raw = file_get_contents($file);
                        $obj = json_decode($raw);
                        $cont = $obj->cont;
                        $favs = $obj->favs;
                        if ($cont == $pass) {
                            $obj = new stdClass();
                            $obj->cont = $pass;
                            $obj->favs = $favs;
                            $obj->vistos = $_GET["code"];
                            $json = json_encode($obj);
                            if (file_put_contents($file, $json) != false) {
                                $nraw = file_get_contents($file);
                                echo $nraw;
                            } else {
                                echo "Error";
                            }
                        } else {
                            echo "Contraseña";
                        }
                    } else {
                        echo "No Existe";
                    }
                } else {
                    echo "Falta informacion";
                }
            }

            if ($tipo == $list) {
                $listFile = "../user_favs/list.json";
                if (file_exists($listFile)) {
                    header('Content-Type: application/json');
                    echo file_get_contents("../user_favs/list.json");
                } else {
                    $json = new stdClass();
                    $array = array();
                    $json->list = $array;
                    header('Content-Type: application/json');
                    echo json_encode($json,JSON_PRETTY_PRINT);
                }
            }

            if ($tipo == $addUser){
                if (isset($_GET["email_coded"])){
                    $user = '../user_favs/'.$_GET['email_coded'].".txt";
                    if (file_exists($user)){
                        saveEmail();
                        $response = new stdClass();
                        $response->response = "ok";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }else{
                        $response = new stdClass();
                        $response->response = "not-exist";
                        header('Content-Type: application/json');
                        echo json_encode($response);
                    }
                }
            }
        } else {
            echo "Que haces aqui??? 7_7";
        }
    } else {
        header('HTTP/1.0 403 Forbidden');
        echo "No estas autorizado para usar esta api";
    }
} else {
    header('HTTP/1.0 403 Forbidden');
    echo "Falta el certificado";
}

function saveEmail(){
    $listFile = "../user_favs/list.json";
    if (isset($_GET["email_normal"])) {
        $email = $_GET["email_normal"];
        if (file_exists($listFile)) {
            $json = json_decode(file_get_contents($listFile));
            $list = $json->list;
            if (!in_array($email, $list)) {
                array_push($list, $email);
                $json->list = $list;
                file_put_contents($listFile, json_encode($json,JSON_PRETTY_PRINT));
            }
        } else {
            $json = new stdClass();
            $list = array();
            array_push($list, $email);
            $json->list = $list;
            file_put_contents($listFile, json_encode($json,JSON_PRETTY_PRINT));
        }
    }
}

function replaceEmail(){
    $listFile = "../user_favs/list.json";
    if (isset($_GET["email_normal"])&&isset($_GET["new_email_normal"])) {
        $email = $_GET["email_normal"];
        $nemail=$_GET["new_email_normal"];
        if (file_exists($listFile)) {
            $json = json_decode(file_get_contents($listFile));
            $list = $json->list;
            if (in_array($email, $list)) {
                $list=str_replace($email,$nemail,$list);
                $json->list = $list;
                file_put_contents($listFile, json_encode($json,JSON_PRETTY_PRINT));
            }
        }
    }
}

?>