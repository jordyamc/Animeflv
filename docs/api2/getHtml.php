<?php
include('simple_html_dom.php');
ini_set('display_errors', E_ALL);
error_reporting(1);
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
        if (isset($_GET['url'])) {
            $url = $_GET['url'];
            if (strpos($url, "/ver/") === false) {
                $repPOS = str_replace("POS", "://", $url);
                $source = @file_get_contents($url);
                $cortado = str_replace("http://animeflv.net/", "", $url);
                $file = "../cache/" . substr($cortado, strpos($cortado, "/") + 1, -5) . ".txt";
                $cache = "0";
                if (strpos($source, "var url_lista") === false) {
                    if (!isset($_GET['bypass'])) {
                        if (file_exists($file)) {
                            $source = file_get_contents($file);
                            $cache = "1";
                            codeJSON($source, $cache);
                        } else {
                            $url = $bypasser . $url;
                            $source = @file_get_contents($url);
                            if (isJson($source)) {
                                file_put_contents("cookie.txt", $source);
                                $source = @file_get_contents($url);
                                if (strpos($source, "var url_lista") === false) {
                                    if (file_exists($file)) {
                                        $source = file_get_contents($file);
                                        $cache = "1";
                                        codeJSON($source, $cache);
                                    } else {
                                        echo "error";
                                    }
                                } else {
                                    if (file_exists($file)) {
                                        $cont = file_get_contents($file);
                                        if ($source !== $cont) {
                                            file_put_contents($file, $source);
                                            codeJSON($source, $cache);
                                        }
                                        codeJSON($source, $cache);
                                    } else {
                                        file_put_contents($file, $source);
                                        codeJSON($source, $cache);
                                    }
                                }
                            } else {
                                if (file_exists($file)) {
                                    $cont = file_get_contents($file);
                                    if ($source !== $cont) {
                                        file_put_contents($file, $source);
                                        codeJSON($source, $cache);
                                    }
                                    codeJSON($source, $cache);
                                } else {
                                    file_put_contents($file, $source);
                                    codeJSON($source, $cache);
                                }
                            }
                            //echo "error";
                        }
                    } else {
                        $url = $bypasser . $url;
                        $source = file_get_contents($url);
                        if (isJson($source)) {
                            file_put_contents("cookie.txt", $source);
                            $source = @file_get_contents($url);
                            if (strpos($source, "var url_lista") === false) {
                                if (file_exists($file)) {
                                    $source = @file_get_contents($file);
                                    $cache = "1";
                                    codeJSON($source, $cache);
                                } else {
                                    echo "error";
                                }
                            } else {
                                if (file_exists($file)) {
                                    $cont = file_get_contents($file);
                                    if ($source !== $cont) {
                                        file_put_contents($file, $source);
                                        codeJSON($source, $cache);
                                    }
                                    codeJSON($source, $cache);
                                } else {
                                    file_put_contents($file, $source);
                                    codeJSON($source, $cache);
                                }
                            }
                        } else {
                            if (file_exists($file)) {
                                $cont = file_get_contents($file);
                                if ($source !== $cont) {
                                    file_put_contents($file, $source);
                                    codeJSON($source, $cache);
                                }
                                codeJSON($source, $cache);
                            } else {
                                file_put_contents($file, $source);
                                codeJSON($source, $cache);
                            }
                        }
                    }
                } else {
                    if (file_exists($file)) {
                        $cont = file_get_contents($file);
                        if ($source !== $cont) {
                            file_put_contents($file, $source);
                            codeJSON($source, $cache);
                        } else {
                            codeJSON($source, $cache);
                        }
                    } else {
                        file_put_contents($file, $source);
                        codeJSON($source, $cache);
                    }
                }
            } else {
                $source = @file_get_contents($url);
                $cortado = str_replace("http://animeflv.net/ver", "", $url);
                $file = "../cache/videos/" . substr($cortado, strpos($cortado, "/") + 1, -5) . ".txt";
                $cache = "0";
                if (strpos($source, "var url_lista") === false) {
                    if (!isset($_GET['bypass'])) {
                        if (file_exists($file)) {
                            $source = file_get_contents($file);
                            $cache = "1";
                            codeDown($source, $cache, $file);
                        } else {
                            $url = $bypasser . $url;
                            $source = file_get_contents($url);
                            if (isJson($source)) {
                                file_put_contents("cookie.txt", $source);
                                $source = @file_get_contents($url);
                                if (strpos($source, "var url_lista") === false) {
                                    if (file_exists($file)) {
                                        $source = file_get_contents($file);
                                        $cache = "1";
                                        codeDown($source, $cache, $file);
                                    } else {
                                        echo "error";
                                    }
                                } else {
                                    if (file_exists($file)) {
                                        $cont = file_get_contents($file);
                                        if ($source !== $cont) {
                                            file_put_contents($file, $source);
                                            codeDown($source, $cache, $file);
                                        }
                                        codeDown($source, $cache, $file);
                                    } else {
                                        file_put_contents($file, $source);
                                        codeDown($source, $cache, $file);
                                    }
                                }
                            } else {
                                if (file_exists($file)) {
                                    $cont = file_get_contents($file);
                                    if ($source !== $cont) {
                                        file_put_contents($file, $source);
                                        codeDown($source, $cache, $file);
                                    }
                                    codeDown($source, $cache, $file);
                                } else {
                                    file_put_contents($file, $source);
                                    codeDown($source, $cache, $file);
                                }
                            }
                            //echo "Error en pagina, sin cache";
                        }
                    } else {
                        $url = $bypasser . $url;
                        $source = file_get_contents($url);
                        if (strpos($source, "var url_lista") === false) {
                            if (file_exists($file)) {
                                $source = file_get_contents($file);
                                $cache = "1";
                                codeDown($source, $cache, $file);
                            } else {
                                echo "error";
                            }
                        } else {
                            if (file_exists($file)) {
                                $cont = file_get_contents($file);
                                if ($source !== $cont) {
                                    file_put_contents($file, $source);
                                    codeDown($source, $cache, $file);
                                }
                                codeDown($source, $cache, $file);
                            } else {
                                file_put_contents($file, $source);
                                codeDown($source, $cache, $file);
                            }
                        }
                    }
                } else {
                    if (file_exists($file)) {
                        $cont = file_get_contents($file);
                        if ($source !== $cont) {
                            file_put_contents($file, $source);
                            codeDown($source, $cache, $file);
                        } else {
                            codeDown($source, $cache, $file);
                        }
                    } else {
                        file_put_contents($file, $source);
                        codeDown($source, $cache, $file);
                    }
                }
            }
        } else {
            $url = "http://animeflv.net";
            $repPOS = str_replace("POS", "://", $url);
            $source = @file_get_contents($url);
            $cache = "0";
            if (strpos($source, "lista_completa") === false) {
                if (!isset($_GET['bypass'])) {
                    if (file_exists("../cache/inicio.txt")) {
                        $source = file_get_contents("../cache/inicio.txt");
                        $cache = "1";
                        codeInicio($source, $cache);
                    } else {
                        $url = $bypasser . "http://animeflv.net";
                        $source = file_get_contents($url);
                        if (isJson($source)) {
                            file_put_contents("cookie.txt", $source);
                            $source = @file_get_contents($url);
                            if (strpos($source, "<title>Anime Online - AnimeFLV</title>") === false) {
                                if (file_exists("../cache/inicio.txt")) {
                                    $source = file_get_contents("../cache/inicio.txt");
                                    $cache = "1";
                                    codeInicio($source, $cache);
                                } else {
                                    echo "error";
                                }
                            } else {
                                if (file_exists("../cache/inicio.txt")) {
                                    $cont = file_get_contents("../cache/inicio.txt");
                                    if ($source !== $cont) {
                                        file_put_contents("../cache/inicio.txt", $source);
                                        codeInicio($source, $cache);
                                    }
                                    codeInicio($source, $cache);
                                } else {
                                    file_put_contents("../cache/inicio.txt", $source);
                                    codeInicio($source, $cache);
                                }
                            }
                        } else {
                            if (file_exists("../cache/inicio.txt")) {
                                $cont = file_get_contents("../cache/inicio.txt");
                                if ($source !== $cont) {
                                    file_put_contents("../cache/inicio.txt", $source);
                                    codeInicio($source, $cache);
                                }
                                codeInicio($source, $cache);
                            } else {
                                file_put_contents("../cache/inicio.txt", $source);
                                codeInicio($source, $cache);
                            }
                        }
                        //echo "error";
                    }
                } else {
                    $url = $bypasser . "http://animeflv.net";
                    $source = file_get_contents($url);
                    if (strpos($source, "<title>Anime Online - AnimeFLV</title>") === false) {
                        if (file_exists("../cache/inicio.txt")) {
                            $source = file_get_contents("../cache/inicio.txt");
                            $cache = "1";
                            codeInicio($source, $cache);
                        } else {
                            echo "error";
                        }
                    } else {
                        if (file_exists("../cache/inicio.txt")) {
                            $cont = file_get_contents("../cache/inicio.txt");
                            if ($source !== $cont) {
                                file_put_contents("../cache/inicio.txt", $source);
                            }
                            codeInicio($source, $cache);
                        } else {
                            file_put_contents("../cache/inicio.txt", $source);
                            codeInicio($source, $cache);
                        }
                    }
                }
            } else {
                if (file_exists("../cache/inicio.txt")) {
                    $cont = file_get_contents("../cache/inicio.txt");
                    if ($source !== $cont) {
                        file_put_contents("../cache/inicio.txt", $source);
                    }
                    codeInicio($source, $cache);
                } else {
                    file_put_contents("../cache/inicio.txt", $source);
                    codeInicio($source, $cache);
                }
            }
        }
        if (isset($_GET["data"])) {
            $source = urldecode($_GET["data"]);
            $cache = "0";
            if (strpos($source, "<title>Anime Online - AnimeFLV</title>") === false) {
                if (file_exists("../cache/inicio.txt")) {
                    $source = file_get_contents("../cache/inicio.txt");
                    $cache = "1";
                    codeInicio($source, $cache);
                } else {
                    echo "error";
                }
            } else {
                if (file_exists("../cache/inicio.txt")) {
                    $cont = file_get_contents("../cache/inicio.txt");
                    if ($source !== $cont) {
                        file_put_contents("../cache/inicio.txt", $source);
                        codeInicio($source, $cache);
                    }
                    codeInicio($source, $cache);
                } else {
                    file_put_contents("../cache/inicio.txt", $source);
                    codeInicio($source, $cache);
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

function codeDown($source, $cache, $file)
{//<------Provicional
    preg_match_all('/animeflv.net\/img\/mini\/(.*?).jpg/s', $source, $aid, PREG_PATTERN_ORDER);
    preg_match_all('/<h1>(.*?)<\/h1>/s', $source, $titulo, PREG_PATTERN_ORDER);
    $tit = substr($titulo[1][0], 0, strrpos($titulo[1][0], " "));
    $num = substr($titulo[1][0], strrpos($titulo[1][0], " ") + 1);
    $Jtitulo = $tit;
    $Jeid = $aid[1][0] . "_" . $num . "E";
    $cachefile = "../cache/eid/" . $Jeid . ".txt";
    $Jizanagi = "null";
    $Jzippy = "null";
    $JzippyNo = "null";
    $Zippydata = "null";
    $Jmp4 = "null";
    $Jnow = "null";
    $Jsync = "null";
    $Jmega = "null";
    $Jaflv = "null";
    $Jmaru = "null";
    $JYotta = "null";
    $JYotta480 = "null";
    $JYotta360 = "null";
    $downsname = array();
    $downsurl = array();
    array_push($downsname, "Izanagi");
    array_push($downsname, "Yotta");
    array_push($downsname, "Yotta 480p");
    array_push($downsname, "Yotta 360p");
    array_push($downsname, "Mp4Upload");
    array_push($downsname, "NowVideo");
    array_push($downsname, "ZippyShare");
    array_push($downsname, "ZippyShare Fast");
    array_push($downsname, "Mp4Upload");
    array_push($downsname, "4Sync");
    array_push($downsname, "Mega");
    array_push($downsname, "Animeflv");
    array_push($downsname, "Maru");
    if ($cache === "0" or $cache === "2") {
        if (strpos($source, "izanagi.php?") !== false and $cache === "0") {
            preg_match_all('/embed_izanagi.php\?key=(.*?)"/s', $source, $izanagi, PREG_PATTERN_ORDER);
            $Jizanagi = file_get_contents("https://animeflv.net/embed_izanagi.php?key=" . str_replace('\\', "", $izanagi[1][0]));
            preg_match_all('/get\(\'(.*?)\'/s', $Jizanagi, $izanagi, PREG_PATTERN_ORDER);
            $Jizanagi = file_get_contents(str_replace('\\', "", $izanagi[1][0]));
            $json = json_decode($Jizanagi);
            $Jizanagi = $json->file;
            if (is_null($Jizanagi)){
                $Jizanagi="null";
            }
        }
        if (strpos($source, "yotta.php?") !== false and $cache === "0") {
            preg_match_all('/embed_yotta\.php\?key\=(.*?)\\"/s', $source, $yotta, PREG_PATTERN_ORDER);
            $JYottaurl = "http://s1.animeflv.net/yotta.php?id=" . str_replace('\\', "", $yotta[1][0]);
            $yottaarray = json_decode(file_get_contents($JYottaurl))->sources;
            if (count($yottaarray)>1){
                foreach ($yottaarray as $obj){
                    if ($obj->label==="360p"){
                        $JYotta360=$obj->file;
                        if (is_null($JYotta360)) {
                            $JYotta360 = "null";
                        }
                    }else if ($obj->label==="480p"){
                        $JYotta480=$obj->file;
                        if (is_null($JYotta480)) {
                            $JYotta480 = "null";
                        }
                    }
                }
            }else{
                $JYotta=$yottaarray[0]->file;
            }
            if (is_null($JYotta)) {
                $JYotta = "null";
            }
        }
        if (strpos($source, "zippyshare.com") !== false) {
            preg_match_all('/class="opcion"><a href="(.*?)"/s', $source, $zippy, PREG_PATTERN_ORDER);
            $url = str_replace('\\', "", $zippy[1][1]);
            if ($url !== "") {
                preg_match_all('/http:\/\/mil.ink\/s\/(.*?)\?s=/s', $url, $zippy1, PREG_PATTERN_ORDER);
                $url = str_replace('http://mil.ink/s/' . $zippy1[1][0] . "?s=", "", $zippy[1][1]);
                $Jzippy = $url;
                /*$dataZippy = file_get_contents($url);
                if (strpos($dataZippy, 'video id="video"') !== false) {
                    preg_match_all('/source src="(.*?)" data-res/s', $dataZippy, $zippyf, PREG_PATTERN_ORDER);
                    if (isset($_GET["test"]))
                        var_dump($http_response_header);
                    foreach ($http_response_header as $data) {
                        if (strpos($data, "JSESSIONID") !== false) {
                            $Zippydata = str_replace("Set-Cookie: ", "", $data);
                            $JzippyNo = $zippyf[1][0];
                            break;
                        }
                    }
                } else {
                    $Jzippy = $url;
                }*/
            }
        }
        if (strpos($source, "embed.nowvideo.sx") !== false) {
            $semi = substr($source, strpos($source, "embed.nowvideo.sx"));
            preg_match_all('/embed.nowvideo.sx(.*?)&width/s', $semi, $now, PREG_PATTERN_ORDER);
            $url = "http://embed.nowvideo.sx" . str_replace('\\', "", $now[1][0]);
            $cont = file_get_contents($url);
            $semi = substr($cont, strpos($cont, "<source"));
            preg_match_all('/<source src="(.*?)mp4" type/s', $cont, $now, PREG_PATTERN_ORDER);
            //$Jnow=$now[1][0]."mp4";
        }
        if (isset($_GET['test']))
            if (strpos($source, "www.mp4upload.com") !== false) {
                $semi = substr($source, strpos($source, "www.mp4upload.com"));
                preg_match_all('/www.mp4upload.com(.*?).html/s', $semi, $mp4, PREG_PATTERN_ORDER);
                $origin = str_replace('\\', "", $mp4[1][0]);
                $url = "http://www.mp4upload.com" . str_replace('embed-', '', str_replace('\\', "", $mp4[1][0])) . ".html";
                $cont = file_get_contents($url);
                $semi = substr($cont, strpos($cont, "<form"));
                $json = new stdClass();
                $data = array();
                preg_match_all('/name="op" value="(.*?)">/s', substr($semi, strpos($semi, 'name="op" value="')), $mp4, PREG_PATTERN_ORDER);
                $sub = new stdClass();
                $sub->key = "op";
                $sub->value = $mp4[1][0];
                array_push($data, $sub);
                preg_match_all('/name="id" value="(.*?)">/s', substr($semi, strpos($semi, 'name="id" value="')), $mp4, PREG_PATTERN_ORDER);
                $sub = new stdClass();
                $sub->key = "id";
                $sub->value = $mp4[1][0];
                array_push($data, $sub);
                $sub = new stdClass();
                $sub->key = "rand";
                $sub->value = "";
                array_push($data, $sub);
                $sub = new stdClass();
                $sub->key = "referer";
                $sub->value = "http://www.mp4upload.com" . $origin . ".html";
                array_push($data, $sub);
                $sub = new stdClass();
                $sub->key = "method_free";
                $sub->value = "";
                array_push($data, $sub);
                $sub = new stdClass();
                $sub->key = "method_premium";
                $sub->value = "";
                array_push($data, $sub);
                $json->data = $data;
                $Jmp4 = "http://api2.animeflvapp.xyz/download-helper.php?down_url=" . $url . "&json_data=" . urlencode(json_encode($json));
            }

        if (strpos($source, "4sync.com") !== false) {
            preg_match_all('/file=(.*?)\?sbsr=/s', $source, $sync, PREG_PATTERN_ORDER);
            $Jsync = str_replace('\\', "", $sync[1][0]);
            $headers = get_headers($Jsync);
            if (strpos($headers[0], "200") === false) {
                $Jsync = "null";
            }
        }
        if (strpos($source, "https://mega.nz/") !== false) {
            preg_match_all('/https:\/\/mega.nz\/(.*?)"/s', $source, $mega, PREG_PATTERN_ORDER);
            $Jmega = "https://mega.nz/" . $mega[1][0];
        }
        if (strpos($source, "subidas.com") !== false) {
            preg_match_all('/href="http:\/\/subidas.com(.*?).mp4/s', $source, $aflv, PREG_PATTERN_ORDER);
            $Jaflv = "http://subidas.com" . str_replace('\\', "", $aflv[1][0]) . ".mp4";
        }
        if (strpos($source, "datacloudmail.ru") !== false) {
            preg_match_all('/cloclo18(.*?).mp4/s', $source, $maru, PREG_PATTERN_ORDER);
            $Jmaru = "https://cloclo18" . str_replace('\\', "", $maru[1][0]) . ".mp4";
        }
        array_push($downsurl, $Jizanagi);
        array_push($downsurl, $JYotta);
        array_push($downsurl, $JYotta480);
        array_push($downsurl, $JYotta360);
        array_push($downsurl, $Jmp4);
        array_push($downsurl, $Jnow);
        array_push($downsurl, $Jzippy);
        array_push($downsurl, $JzippyNo);
        array_push($downsurl, $Jsync);
        array_push($downsurl, $Jmega);
        array_push($downsurl, $Jaflv);
        array_push($downsurl, $Jmaru);
        $obj = new stdClass();
        $obj->version = file_get_contents("../ver.txt");
        $obj->cache = $cache;
        $obj->titulo = $Jtitulo;
        $obj->numero = $num;
        $obj->eid = $Jeid;
        $downarray = array();
        for ($i = 0; $i < count($downsname); $i++) {
            $d = new stdClass();
            $d->name = $downsname[$i];
            $d->url = $downsurl[$i];
            if ($downsname[$i] == "ZippyShare Fast" and $Zippydata != "null") {
                $d->data = $Zippydata;
            }
            array_push($downarray, $d);
        }
        $obj->downloads = $downarray;
        header('Content-Type: application/json');
        echo json_encode($obj);
        if ($cache === "0") {
            file_put_contents($cachefile, json_encode($obj));
        }
    } else if (file_exists($cachefile)) {
        header('Content-Type: application/json');
        echo file_get_contents($cachefile);
    } else if (file_exists($file) and $cache !== "2") {
        codeDown(file_get_contents($file), "2", $file);
    } else {
        array_push($downsurl, $Jizanagi);
        array_push($downsurl, $JYotta);
        array_push($downsurl, $JYotta480);
        array_push($downsurl, $JYotta360);
        array_push($downsurl, $Jmp4);
        array_push($downsurl, $Jnow);
        array_push($downsurl, $Jzippy);
        array_push($downsurl, $JzippyNo);
        array_push($downsurl, $Jsync);
        array_push($downsurl, $Jmega);
        array_push($downsurl, $Jaflv);
        array_push($downsurl, $Jmaru);
        $obj = new stdClass();
        $obj->version = file_get_contents("../ver.txt");
        $obj->cache = $cache;
        $obj->titulo = $Jtitulo;
        $obj->numero = $num;
        $obj->eid = $Jeid;
        $downarray = array();
        for ($i = 0; $i < count($downsname); $i++) {
            $d = new stdClass();
            $d->name = $downsname[$i];
            $d->url = $downsurl[$i];
            if ($downsname[$i] == "ZippyShare Fast" and $Zippydata != "null") {
                $d->data = $Zippydata;
            }
            array_push($downarray, $d);
        }
        $obj->downloads = $downarray;
        header('Content-Type: application/json');
        echo json_encode($obj);
    }
}

function codeJSON($source, $cache)
{ //<------Provicional
    preg_match_all('/cdn.animeflv.net\/img\/portada\/(.*?).jpg/s', $source, $aid, PREG_PATTERN_ORDER);
    preg_match_all('/Tipo:<\/b>(.*?)<\/li>/s', $source, $tid, PREG_PATTERN_ORDER);
    preg_match_all('/<h1>(.*?)<\/h1>/s', $source, $titulo, PREG_PATTERN_ORDER);
    preg_match_all('/class="serie_estado_(.*?)">/s', $source, $estado, PREG_PATTERN_ORDER);
    preg_match_all('/href="\/animes\/genero\/(.*?)\/"/s', $source, $generos, PREG_PATTERN_ORDER);
    preg_match_all('/id="listado_epis".*?<li>(.*?)<\/li><\/ul>/s', $source, $episodiosPar, PREG_PATTERN_ORDER);
    preg_match_all('/class="sinopsis">(.*?)<\/div>/s', $source, $sinopsis, PREG_PATTERN_ORDER);
    preg_match_all('/class="relacionados">(.*?)<\/div>/s', $source, $rels, PREG_PATTERN_ORDER);
    preg_match_all('/<b>(.*?)<\/b>/s', $rels[1][0], $rel_tipos, PREG_PATTERN_ORDER);
    preg_match_all('/\((.*?)\)/s', $rels[1][0], $tids, PREG_PATTERN_ORDER);
    preg_match_all('/">(.*?)<\/a>/s', $rels[1][0], $rel_tits, PREG_PATTERN_ORDER);
    preg_match_all('/href="\/(.*?).html">/s', $rels[1][0], $rel_aids, PREG_PATTERN_ORDER);
    preg_match_all('/Inicio:<\/b>(.*?)<\/li>/s', $source, $finicio, PREG_PATTERN_ORDER);
    $Jaid = $aid[1][0];
    $Jtid = $tid[1][0];
    $Jtitulo = $titulo[1][0];
    $Jsinopsis = $sinopsis[1][0];
    if ($estado[1][0] == 1) {
        $Jestado = "0000-00-00";
    } else if ($estado[1][0] == 3) {
        $Jestado = "prox";
    } else {
        $Jestado = "1";
    }
    $Jgeneros = "";
    foreach ($generos[1] as $g) {
        $Jgeneros = $Jgeneros . ucfirst($g) . ", ";
    }
    $Jepisodios = array();
    $html=str_get_html($episodiosPar[1][0]);
    foreach ($html->find('a') as $link) {
        $ep = $link->href;
        $json = new stdClass();
        $json->num = substr($ep, strrpos($ep, "-") + 1);
        $json->eid = $Jaid . "_" . substr($ep, strrpos($ep, "-") + 1) . "E";
        $sfile = "../cache/eid/" . $Jaid . "_" . substr($ep, strrpos($ep, "-") + 1) . "E" . ".txt";
        if (file_exists($sfile)) {
            $file = file_get_contents($sfile);
            $dec = json_decode($file);
            $part = $dec->downloads;
        } else {
            $part = "null";
        }
        $json->downloads = $part;
        array_push($Jepisodios, $json);
    }
    $Jrelacionados = array();
    $htmlrels=str_get_html($rels[1][0]);
    $i=0;
    foreach ($htmlrels->find('a') as $rel){
        $link=$rel->href;
        $t_html=file_get_contents("http://animeflv.net".$link);
        preg_match_all('/cdn.animeflv.net\/img\/portada\/(.*?).jpg/s', $t_html, $aid_rel, PREG_PATTERN_ORDER);
        preg_match_all('/(?<=\()(.+)(?=\))/is', $htmlrels->find('li')[$i]->plaintext, $t_tid, PREG_PATTERN_ORDER);
        $aid=$aid_rel[1][0];
        $titulo = $rel_tits[1][$i];
        $tid = $t_tid[1][0];
        $tipo = $rel_tipos[1][$i];
        $j = new stdClass();
        $j->aid = $aid;
        $j->tid = $tid;
        $j->titulo = $titulo;
        $j->rel_tipo = $tipo;
        array_push($Jrelacionados, $j);
        $i++;
    }
    $cache = "0";
    $obj = new stdClass();
    $obj->cache = $cache;
    $obj->aid = $Jaid;
    $obj->tid = substr($Jtid, 1);
    $obj->titulo = $Jtitulo;
    $obj->sinopsis = $Jsinopsis;
    $obj->fecha_inicio = str_replace(" ", "", $finicio[1][0]);
    $obj->fecha_fin = $Jestado;
    if (substr($Jgeneros, 0, -2) === "") {
        $obj->generos = "Sin-Generos";
    } else {
        $obj->generos = substr($Jgeneros, 0, -2);
    }
    $obj->episodios = $Jepisodios;
    $obj->relacionados = $Jrelacionados;
    header('Content-Type: application/json');
    echo json_encode($obj);
    $file = "../cache/aid/" . $Jaid . ".txt";
    file_put_contents($file, json_encode($obj));
}

function codeInicio($source, $cache)
{//<------Provicional
    $aids = array();
    $titulos = array();
    $numeros = array();
    $tids = array();
    $eids = array();
    $estado = array();
    $urlnormal = array();
    $urlbypass = array();
    preg_match_all('/ultimos_epis(.*?)bloque_der/s', $source, $frags, PREG_PATTERN_ORDER);
    preg_match_all('/class=\"not(.*?)an><\/span>/s', $frags[1][0], $caps, PREG_PATTERN_ORDER);
    foreach ($caps[1] as $fragmento) {
        preg_match_all('/img\/mini\/(.*?).jpg/s', $fragmento, $aid, PREG_PATTERN_ORDER);
        preg_match_all('/title=\"(.*?)\">/s', $fragmento, $tit, PREG_PATTERN_ORDER);
        preg_match_all('/class=\"tit\"\>(.*?)<\/sp/s', $fragmento, $titulo, PREG_PATTERN_ORDER);
        preg_match_all('/a href=\"(.*?)\" title/s', $fragmento, $links);
        $isAnime = false;
        if (strpos($fragmento, 'tova') !== false) {
            array_push($tids, "OVA");
        } else {
            if (strpos($fragmento, 'tpeli') !== false) {
                array_push($tids, "Pelicula");
            } else {
                array_push($tids, "Anime");
                $isAnime = true;
            }
        }
        $finurl = str_replace("/ver/", "", $links[1][0]);
        $finurl = substr($finurl, 0, strrpos($finurl, "-"));
        $html = @file_get_contents('../cache/' . $finurl . '.txt');
        preg_match_all('/class="serie_estado_(.*?)">/s', $html, $Aestado, PREG_PATTERN_ORDER);
        $file = '../times/' . $aid[1][0] . '.json';
        if (!file_exists($file)) {
            if (!isset($_GET['bypass']) and $cache == "0" and is_numeric(substr($tit[1][0], strrpos($tit[1][0], " ") + 1))) {
                $d = $aid[1][0];
                $file = '../times/' . $d . '.json';
                $json = new stdClass();
                $json->aid = $d;
                $json->daycode = date('N');
                $json->hour = date('~h:iA');
                file_put_contents($file, json_encode($json));
            }
        }
        if (@$Aestado[1][0] != 2) {
            $Jestado = "0000-00-00";
            $file = "../times/list/" . $aid[1][0] . ".data";
            if (!file_exists($file) and $isAnime and is_numeric(substr($tit[1][0], strrpos($tit[1][0], " ") + 1))) {
                $njson = new stdClass();
                $njson->aid = $aid[1][0];
                $njson->titulo = substr($tit[1][0], 0, strrpos($tit[1][0], " "));
                $njson->daycode = date('N');
                $njson->hour = date('~h:iA');
                file_put_contents($file, json_encode($njson));
            }
        } else {
            $Jestado = "1";
            $file = "../times/list/" . $aid[1][0] . ".data";
            if (file_exists($file)) {
                unlink($file);
            }
        }
        array_push($aids, $aid[1][0]);
        array_push($titulos, substr($tit[1][0], 0, strrpos($tit[1][0], " ")));
        array_push($numeros, substr($tit[1][0], strrpos($tit[1][0], " ") + 1));
        array_push($eids, $aid[1][0] . "_" . substr($titulo[1][0], strrpos($titulo[1][0], " ") + 1) . "E");
        array_push($estado, $Jestado);
        array_push($urlnormal, curPageURL() . "&url=http://animeflv.net" . $links[1][0]);
        array_push($urlbypass, curPageURL() . "&url=http://animeflv.net" . $links[1][0] . "&bypass");
    }

    $FJSON = array();
    for ($i = 0; $i < count($aids); $i++) {
        $json = new stdClass();
        $json->aid = $aids[$i];
        $json->titulo = $titulos[$i];
        $json->numero = $numeros[$i];
        $json->tid = $tids[$i];
        $json->eid = $eids[$i];
        $json->estado = $estado[$i];
        if (!isset($_GET['hide'])) {
            $json->normal = $urlnormal[$i];
            $json->bypass = $urlbypass[$i];
        }
        if (is_numeric($numeros[$i])) {
            array_push($FJSON, $json);
        }
    }
    $last = '00:00';
    if ($cache === '0') {
        $last = date('h:iA');
        file_put_contents('../times/last.txt', $last);
    } else {
        $last = file_get_contents('../times/last.txt');
    }
    $JS = new stdClass();
    $JS->version = file_get_contents("../ver.txt");
    $JS->cache = $cache;
    $JS->last = $last;
    $JS->lista = $FJSON;
    header('Content-Type: application/json');
    echo json_encode($JS);
}

function get_http_response_code($theURL)
{
    $headers = get_headers($theURL);
    return substr($headers[0], 9, 3);
}

function curPageURL()
{
    $pageURL = 'http';
    if (@$_SERVER["HTTPS"] == "on") {
        $pageURL .= "s";
    }
    $pageURL .= "://";
    if ($_SERVER["SERVER_PORT"] != "80") {
        $pageURL .= $_SERVER["SERVER_NAME"] . ":" . $_SERVER["SERVER_PORT"] . $_SERVER["REQUEST_URI"];
    } else {
        $pageURL .= $_SERVER["SERVER_NAME"] . $_SERVER["REQUEST_URI"];
    }
    return str_replace("&bypass", "", $pageURL);
}

function isJson($string)
{
    json_decode($string);
    return (json_last_error() == JSON_ERROR_NONE);
}

?>