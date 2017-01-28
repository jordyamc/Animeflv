<?php
ini_set('display_errors',1); error_reporting(E_ALL);
$use_scraper=false;
$bypasser="http://appapi.seriesgt.com/v1/content?type=html&url=";
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
if ($resultado->num_rows != 0 ){
if (isset($_GET['url'])){
    $url=$_GET['url'];
    if (strpos($url,"/ver/")===false){
        $repPOS=str_replace("POS","://",$url);
        $source= @file_get_contents($url);
        $cortado = str_replace("http://animeflv.net/","",$url);
        $file="../cache/".substr($cortado,strpos($cortado,"/")+1,-5).".txt";
        $cache="0";
        if (strpos($source,"var url_lista")===false){
            if (!isset($_GET['bypass'])){
                if (file_exists($file)){
                    $source=file_get_contents($file);
                    $cache="1";
                    codeJSON($source,$cache);
                }
                else{
                    $url=$bypasser.$url;
                $source= file_get_contents($url);
                if (isJson($source)){
                    file_put_contents("cookie.txt",$source);
                    $source= @file_get_contents($url);
                    if (strpos($source,"var url_lista")===false){
                    if (file_exists($file)){
                        $source=file_get_contents($file);
                        $cache="1";
                        codeJSON($source,$cache);
                        }
                        else{
                            echo "error";
                        }
                    }
                    else{
                        if (file_exists($file)){
                            $cont=file_get_contents($file);
                            if ($source!==$cont){
                                file_put_contents($file,$source);
                                codeJSON($source,$cache);
                            }
                            codeJSON($source,$cache);
                        }
                        else{
                            file_put_contents($file,$source);
                            codeJSON($source,$cache);
                        }
                    }
                }
                else{
                    if (file_exists($file)){
                        $cont=file_get_contents($file);
                        if ($source!==$cont){
                            file_put_contents($file,$source);
                            codeJSON($source,$cache);
                        }
                        codeJSON($source,$cache);
                    }
                    else{
                        file_put_contents($file,$source);
                        codeJSON($source,$cache);
                    }
                }
                    //echo "error";
                }
            }
            else{
                $url=$bypasser.$url;
                $source= file_get_contents($url);
                if (isJson($source)){
                    file_put_contents("cookie.txt",$source);
                    $source= @file_get_contents($url);
                    if (strpos($source,"var url_lista")===false){
                    if (file_exists($file)){
                        $source=file_get_contents($file);
                        $cache="1";
                        codeJSON($source,$cache);
                        }
                        else{
                            echo "error";
                        }
                    }
                    else{
                        if (file_exists($file)){
                            $cont=file_get_contents($file);
                            if ($source!==$cont){
                                file_put_contents($file,$source);
                                codeJSON($source,$cache);
                            }
                            codeJSON($source,$cache);
                        }
                        else{
                            file_put_contents($file,$source);
                            codeJSON($source,$cache);
                        }
                    }
                }
                else{
                    if (file_exists($file)){
                        $cont=file_get_contents($file);
                        if ($source!==$cont){
                            file_put_contents($file,$source);
                            codeJSON($source,$cache);
                        }
                        codeJSON($source,$cache);
                    }
                    else{
                        file_put_contents($file,$source);
                        codeJSON($source,$cache);
                    }
                }
            }
        }else{
            if (file_exists($file)){
                $cont=file_get_contents($file);
                if ($source!==$cont){
                    file_put_contents($file,$source);
                    codeJSON($source,$cache);
                }
                else{
                    codeJSON($source,$cache);
                }
            }
            else{
                file_put_contents($file,$source);
                codeJSON($source,$cache);
            }
        }
    }
    else{
        $source= @file_get_contents($url);
        $cortado = str_replace("http://animeflv.net/ver","",$url);
        $file="../cache/videos/".substr($cortado,strpos($cortado,"/")+1,-5).".txt";
        $cache="0";
        if (strpos($source,"var url_lista")===false){
            if (!isset($_GET['bypass'])){
                if (file_exists($file)){
                    $source=file_get_contents($file);
                    $cache="1";
                    codeDown($source,$cache);
                }
                else{
                    $url=$bypasser.$url;
                $source= file_get_contents($url);
                if (isJson($source)){
                    file_put_contents("cookie.txt",$source);
                    $source= @file_get_contents($url);
                    if (strpos($source,"var url_lista")===false){
                    if (file_exists($file)){
                        $source=file_get_contents($file);
                        $cache="1";
                        codeDown($source,$cache);
                        }
                        else{
                            echo "error";
                        }
                    }
                    else{
                        if (file_exists($file)){
                            $cont=file_get_contents($file);
                            if ($source!==$cont){
                                file_put_contents($file,$source);
                                codeDown($source,$cache);
                            }
                            codeDown($source,$cache);
                        }
                        else{
                            file_put_contents($file,$source);
                            codeDown($source,$cache);
                        }
                    }
                }
                else{
                    if (file_exists($file)){
                        $cont=file_get_contents($file);
                        if ($source!==$cont){
                            file_put_contents($file,$source);
                            codeDown($source,$cache);
                        }
                        codeDown($source,$cache);
                    }
                    else{
                        file_put_contents($file,$source);
                        codeDown($source,$cache);
                    }
                }
                    //echo "Error en pagina, sin cache";
                }
            }
            else{
                $url="http://animeflvapp.pythonanywhere.com/v1/content?type=html&url=".$url;
                $source= file_get_contents($url);
                if (isJson($source)){
                    file_put_contents("cookie.txt",$source);
                    $source= @file_get_contents($url);
                    if (strpos($source,"var url_lista")===false){
                    if (file_exists($file)){
                        $source=file_get_contents($file);
                        $cache="1";
                        codeDown($source,$cache);
                        }
                        else{
                            echo "error";
                        }
                    }
                    else{
                        if (file_exists($file)){
                            $cont=file_get_contents($file);
                            if ($source!==$cont){
                                file_put_contents($file,$source);
                                codeDown($source,$cache);
                            }
                            codeDown($source,$cache);
                        }
                        else{
                            file_put_contents($file,$source);
                            codeDown($source,$cache);
                        }
                    }
                }
                else{
                    if (file_exists($file)){
                        $cont=file_get_contents($file);
                        if ($source!==$cont){
                            file_put_contents($file,$source);
                            codeDown($source,$cache);
                        }
                        codeDown($source,$cache);
                    }
                    else{
                        file_put_contents($file,$source);
                        codeDown($source,$cache);
                    }
                }
            }
        }else{
            if (file_exists($file)){
                $cont=file_get_contents($file);
                if ($source!==$cont){
                    file_put_contents($file,$source);
                    codeDown($source,$cache);
                }
                else{
                    codeDown($source,$cache);
                }
            }
            else{
                file_put_contents($file,$source);
                codeDown($source,$cache);
            }
        }
    }
}
else{
    $url="http://animeflv.net";
    $repPOS=str_replace("POS","://",$url);
    $source= @file_get_contents($url);
    $cache="0";
    if (strpos($source,"lista_completa")===false){
        if (!isset($_GET['bypass'])){
            if (file_exists("../cache/inicio.txt")){
                $source=file_get_contents("../cache/inicio.txt");
                $cache="1";
                codeInicio($source,$cache);
            }
            else{
            $url=$bypasser."http://animeflv.net";
            $source= file_get_contents($url);
            if (isJson($source)){
                file_put_contents("cookie.txt",$source);
                $source= @file_get_contents($url);
                if (strpos($source,"<title>Anime Online - AnimeFLV</title>")===false){
                    if (file_exists("../cache/inicio.txt")){
                    $source=file_get_contents("../cache/inicio.txt");
                    $cache="1";
                    codeInicio($source,$cache);
                    }
                    else{
                        echo "error";
                    }
                }
                else{
                    if (file_exists("../cache/inicio.txt")){
                        $cont=file_get_contents("../cache/inicio.txt");
                        if ($source!==$cont){
                            file_put_contents("../cache/inicio.txt",$source);
                            codeInicio($source,$cache);
                        }
                        codeInicio($source,$cache);
                    }
                    else{
                        file_put_contents("../cache/inicio.txt",$source);
                        codeInicio($source,$cache);
                    }
                }
            }
            else{
                if (file_exists("../cache/inicio.txt")){
                    $cont=file_get_contents("../cache/inicio.txt");
                    if ($source!==$cont){
                        file_put_contents("../cache/inicio.txt",$source);
                        codeInicio($source,$cache);
                    }
                    codeInicio($source,$cache);
                }
                else{
                    file_put_contents("../cache/inicio.txt",$source);
                    codeInicio($source,$cache);
                }
            }
                //echo "error";
            }
        }
        else{
            $url=$bypasser."http://animeflv.net";
            $source= file_get_contents($url);
            if (isJson($source)){
                file_put_contents("cookie.txt",$source);
                $source= @file_get_contents($url);
                if (strpos($source,"<title>Anime Online - AnimeFLV</title>")===false){
                    if (file_exists("../cache/inicio.txt")){
                    $source=file_get_contents("../cache/inicio.txt");
                    $cache="1";
                    codeInicio($source,$cache);
                    }
                    else{
                        echo "error";
                    }
                }
                else{
                    if (file_exists("../cache/inicio.txt")){
                        $cont=file_get_contents("../cache/inicio.txt");
                        if ($source!==$cont){
                            file_put_contents("../cache/inicio.txt",$source);
                        }
                        codeInicio($source,$cache);
                    }
                    else{
                        file_put_contents("../cache/inicio.txt",$source);
                        codeInicio($source,$cache);
                    }
                }
            }
            else{
                if (file_exists("../cache/inicio.txt")){
                    $cont=file_get_contents("../cache/inicio.txt");
                    if ($source!==$cont){
                        file_put_contents("../cache/inicio.txt",$source);
                    }
                    codeInicio($source,$cache);
                }
                else{
                    file_put_contents("../cache/inicio.txt",$source);
                    codeInicio($source,$cache);
                }
            }
        }
    }
    else{
        if (file_exists("../cache/inicio.txt")){
            $cont=file_get_contents("../cache/inicio.txt");
            if ($source!==$cont){
                file_put_contents("../cache/inicio.txt",$source);
            }
            codeInicio($source,$cache);
        }
        else{
            file_put_contents("../cache/inicio.txt",$source);
            codeInicio($source,$cache);
        }
    }
}
if (isset($_GET["data"])){
    $source= urldecode($_GET["data"]);
    $cache="0";
    if (strpos($source,"<title>Anime Online - AnimeFLV</title>")===false){
        if (file_exists("../cache/inicio.txt")){
            $source=file_get_contents("../cache/inicio.txt");
            $cache="1";
            codeInicio($source,$cache);
        }
        else{
            echo "error";
        }
    }
    else{
        if (file_exists("../cache/inicio.txt")){
            $cont=file_get_contents("../cache/inicio.txt");
            if ($source!==$cont){
                file_put_contents("../cache/inicio.txt",$source);
                codeInicio($source,$cache);
            }
            codeInicio($source,$cache);
        }
        else{
            file_put_contents("../cache/inicio.txt",$source);
            codeInicio($source,$cache);
        }
    }
}
} else{ 
    header('HTTP/1.0 403 Forbidden');
    echo "No estas autorizado para usar esta api"; 
} } else{ 
    header('HTTP/1.0 403 Forbidden');
    echo "Falta el certificado"; 
}

function codeDown($source,$cache){//<------Provicional
    preg_match_all('/animeflv.net\/img\/mini\/(.*?).jpg/s', $source, $aid,PREG_PATTERN_ORDER);
    preg_match_all('/<h1>(.*?)<\/h1>/s', $source, $titulo,PREG_PATTERN_ORDER);
    $tit=substr($titulo[1][0],0,strrpos($titulo[1][0]," "));
    $num=substr($titulo[1][0],strrpos($titulo[1][0]," ")+1);
    $Jtitulo=$tit;
    $Jnumero=$num;
    $Jeid=$aid[1][0]."_".$num."E";
    $Jizanagi="null";
    $Jzippy="null";
    $Jsync="null";
    $Jmega="null";
    $Jaflv="null";
    $Jmaru="null";
    $downsname=array();
    $downsurl=array();
    array_push($downsname,"Izanagi");
    array_push($downsname,"ZippyShare");
    array_push($downsname,"4Sync");
    array_push($downsname,"Mega");
    array_push($downsname,"Animeflv");
    array_push($downsname,"Maru");
    if (strpos($source,"izanagi.php?")!==false and $cache==="0"){
        preg_match_all('/video\/izanagi.php(.*?)"/s', $source, $izanagi,PREG_PATTERN_ORDER);
        $Jizanagi="https://animeflv.net/d.php?url=http://animeflv.net/video/izanagi.php".str_replace('\\',"",$izanagi[1][0]);
    }
    if (strpos($source,"zippyshare.com")!==false){
        preg_match_all('/&proxy.link=(.*?)file.html/s', $source, $zippy,PREG_PATTERN_ORDER);
        $Jzippy=str_replace('\\',"",$zippy[1][0])."file.html";
    }
    if (strpos($source,"4sync.com")!==false){
        preg_match_all('/file=(.*?)\?sbsr=/s', $source, $sync,PREG_PATTERN_ORDER);
        $Jsync=str_replace('\\',"",$sync[1][0]);
        $headers=get_headers($Jsync);
        if(strpos($headers[0],"200")===false){
            $Jsync="null";
        }
    }
    if (strpos($source,"https://mega.nz/")!==false){
        preg_match_all('/https:\/\/mega.nz\/(.*?)"/s', $source, $mega,PREG_PATTERN_ORDER);
        $Jmega="https://mega.nz/".$mega[1][0];
    }
    if (strpos($source,"subidas.com")!==false){
        preg_match_all('/href="http:\/\/subidas.com(.*?).mp4/s', $source, $aflv,PREG_PATTERN_ORDER);
        $Jaflv="http://subidas.com".str_replace('\\',"",$aflv[1][0]).".mp4";
    }
    if (strpos($source,"datacloudmail.ru")!==false){
        preg_match_all('/cloclo18(.*?).mp4/s', $source, $maru,PREG_PATTERN_ORDER);
        $Jmaru="https://cloclo18".str_replace('\\',"",$maru[1][0]).".mp4";
    }
    array_push($downsurl,$Jizanagi);
    array_push($downsurl,$Jzippy);
    array_push($downsurl,$Jsync);
    array_push($downsurl,$Jmega);
    array_push($downsurl,$Jaflv);
    array_push($downsurl,$Jmaru);
    $obj = new stdClass();
    $obj->version=file_get_contents("../ver.txt");
    $obj->cache=$cache;
    $obj->titulo=$Jtitulo;
    $obj->numero=$num;
    $obj->eid=$Jeid;
    $downarray=array();
    for ($i = 0; $i < count($downsname); $i++) {
        $d = new stdClass();
        $d->name=$downsname[$i];
        $d->url=$downsurl[$i];
        array_push($downarray,$d);
    }
    $obj->downloads=$downarray;
    header('Content-Type: application/json');
    echo json_encode($obj);
    $file="../cache/eid/".$Jeid.".txt";
    file_put_contents($file,json_encode($obj));
}

function codeJSON($source,$cache){ //<------Provicional
    preg_match_all('/cdn.animeflv.net\/img\/portada\/(.*?).jpg/s', $source, $aid,PREG_PATTERN_ORDER);
    preg_match_all('/Tipo:<\/b>(.*?)<\/li>/s', $source, $tid,PREG_PATTERN_ORDER);
    preg_match_all('/<h1>(.*?)<\/h1>/s', $source, $titulo,PREG_PATTERN_ORDER);
    preg_match_all('/class="serie_estado_(.*?)">/s', $source, $estado,PREG_PATTERN_ORDER);
    preg_match_all('/href="\/animes\/genero\/(.*?)\/"/s', $source, $generos,PREG_PATTERN_ORDER);
    preg_match_all('/id="listado_epis".*?<li>(.*?)<\/li><\/ul>/s', $source, $episodiosPar,PREG_PATTERN_ORDER);
    preg_match_all('/href="\/ver\/(.*?).html">/s', $episodiosPar[1][0], $episodios,PREG_PATTERN_ORDER);
    preg_match_all('/class="sinopsis">(.*?)<\/div>/s', $source, $sinopsis,PREG_PATTERN_ORDER);
    preg_match_all('/class="relacionados">(.*?)<\/div>/s', $source, $rels,PREG_PATTERN_ORDER);
    preg_match_all('/<b>(.*?)<\/b>/s', $rels[1][0], $rel_tipos,PREG_PATTERN_ORDER);
    preg_match_all('/\((.*?)\)/s', $rels[1][0], $tids,PREG_PATTERN_ORDER);
    preg_match_all('/">(.*?)<\/a>/s', $rels[1][0], $rel_tits,PREG_PATTERN_ORDER);
    preg_match_all('/href="\/(.*?).html">/s', $rels[1][0], $rel_aids,PREG_PATTERN_ORDER);
    $Jaid=$aid[1][0];
    $Jtid=$tid[1][0];
    $Jtitulo=$titulo[1][0];
    $Jsinopsis=$sinopsis[1][0];
    if($estado[1][0]==1){
        $Jestado="0000-00-00";
    }
    else{
        $Jestado="1";
    }
    $Jgeneros="";
    foreach($generos[1] as $g){
        $Jgeneros=$Jgeneros.ucfirst($g).", ";
    }
    $Jepisodios=array();
    foreach($episodios[1] as $ep){
        $json = new stdClass();
        $json->num=substr($ep,strrpos($ep,"-")+1);
        $json->eid=$Jaid."_".substr($ep,-1)."E";
        array_push($Jepisodios, $json);
    }
    $Jrelacionados=array();
    for ($i = 0; $i < count($rel_tipos[1]); $i++) {
        $file=@file_get_contents("http://animeflv.net/".$rel_aids[1][$i].".html");
        preg_match_all('/Tipo:<\/b>(.*?)<\/li>/s', $file, $tidRel,PREG_PATTERN_ORDER);
        preg_match_all('/cdn.animeflv.net\/img\/portada\/(.*?).jpg/s', $file, $aidRel,PREG_PATTERN_ORDER);
        $aid=@$aidRel[1][0];
        $titulo=$rel_tits[1][$i];
        $tid=$tidRel[1][0];
        $tipo=$rel_tipos[1][$i];
        $j = new stdClass();
        $j->aid=$aid;
        $j->tid=$tid;
        $j->titulo=$titulo;
        $j->rel_tipo=$tipo;
        array_push($Jrelacionados, $j);
    }
    $cache="0";
    $obj = new stdClass();
    $obj->cache=$cache;
    $obj->aid=$Jaid;
    $obj->tid=substr($Jtid,1);
    $obj->titulo=$Jtitulo;
    $obj->sinopsis=$Jsinopsis;
    $obj->fecha_fin=$Jestado;
    $obj->generos=substr($Jgeneros,0,-2);
    $obj->episodios=$Jepisodios;
    $obj->relacionados=$Jrelacionados;
    header('Content-Type: application/json');
    echo json_encode($obj);
    $file="../cache/aid/".$Jaid.".txt";
    file_put_contents($file,json_encode($obj));
}

function codeInicio($source,$cache){//<------Provicional
    $aids=array();
    $titulos=array();
    $numeros=array();
    $tids=array();
    $eids=array();
    $estado=array();
    $dia=array();
    $hora=array();
    $urlnormal=array();
    $urlbypass=array();
    preg_match_all('/ultimos_epis(.*?)bloque_der/s', $source, $frags,PREG_PATTERN_ORDER);
    preg_match_all('/class=\"not(.*?)an><\/span>/s', $frags[1][0], $caps,PREG_PATTERN_ORDER);
    foreach($caps[1] as $fragmento){
        preg_match_all('/img\/mini\/(.*?).jpg/s', $fragmento, $aid,PREG_PATTERN_ORDER);
        preg_match_all('/title=\"(.*?)\">/s', $fragmento, $tit,PREG_PATTERN_ORDER);
        preg_match_all('/class=\"tit\"\>(.*?)<\/sp/s', $fragmento, $titulo,PREG_PATTERN_ORDER);
        preg_match_all('/a href=\"(.*?)\" title/s', $fragmento, $links);
        if (strpos($fragmento,'tova') !== false) {
            array_push($tids, "OVA");
        }
        else{
            if (strpos($fragmento,'tpeli') !== false) {
                array_push($tids, "Pelicula");
            }
            else{
                array_push($tids, "Anime");
            }
        }
        $finurl=str_replace("/ver/","",$links[1][0]);
        $finurl=substr($finurl,0,strrpos($finurl,"-"));
        $html=@file_get_contents('../cache/'.$finurl.'.txt');
        preg_match_all('/class="serie_estado_(.*?)">/s', $html, $Aestado,PREG_PATTERN_ORDER);
        if($Aestado[1][0]==1){
            $Jestado="0000-00-00";
        }
        else{
            $Jestado="1";
        }
        $file='../times/'.$aid[1][0].'.json';
        if (!file_exists($file)){
            file_get_contents('http://api2.animeflvapp.xyz/time.php?aid='.$aid[1][0].'&set');
            $day=date('N');
            $ho=date('~h:iA');
        }
        else{
            $times=json_decode(file_get_contents('../times/'.$aid[1][0].'.json'));
            $day=$times->daycode;
            $ho=$times->hour;
        }
        array_push($aids, $aid[1][0]);
        array_push($titulos, substr($tit[1][0],0,strrpos($tit[1][0]," ")));
        array_push($numeros, substr($tit[1][0],strrpos($tit[1][0]," ")+1));
        array_push($eids, $aid[1][0]."_".substr($titulo[1][0],strrpos($titulo[1][0]," ")+1)."E");
        array_push($estado, $Jestado);
        array_push($dia, $day);
        array_push($hora, $ho);
        array_push($urlnormal, "http://api2.animeflvapp.xyz/getHtml.php?certificate=web&url=http://animeflv.net".$links[1][0]);
        array_push($urlbypass, "http://api2.animeflvapp.xyz/getHtml.php?certificate=web&url=http://animeflv.net".$links[1][0]."&bypass");
    }
    
    $FJSON=array();
    for ($i = 0; $i < count($aids); $i++) {
        $json = new stdClass();
        $json->aid=$aids[$i];
        $json->titulo=$titulos[$i];
        $json->numero=$numeros[$i];
        $json->tid=$tids[$i];
        $json->eid=$eids[$i];
        $json->estado=$estado[$i];
        $json->daycode=$dia[$i];
        $json->hour=$hora[$i];
        if (!isset($_GET['hide'])){
        $json->normal=$urlnormal[$i];
        $json->bypass=$urlbypass[$i];
        }
        array_push($FJSON, $json);
    }
    $JS = new stdClass();
    $JS->version=file_get_contents("../ver.txt");
    $JS->cache=$cache;
    $JS->lista=$FJSON;
    header('Content-Type: application/json');
    echo json_encode($JS);
}

function curl($url) {
	//debug
	//$print = "<curl> ".$url;
    $cookie="Cookie: \r\n";
    $user_agent="Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:39.0) Gecko/20100101 Firefox/39.0";
    if (file_exists("cookie.txt")){
        $json=file_get_contents("cookie.txt");
        if (isJson($json)){
            $json_decoded=json_decode($json);
            $user_agent=$json_decoded->{'user_agent'};
            $cookies_json=$json_decoded->{'tokens_cookies'};
            $encode=json_encode($cookies_json);
            $cookies_json_decoded=json_decode($encode);
            $cfduid=$cookies_json_decoded->{'__cfduid'};
            $cf_clearance=$cookies_json_decoded->{'cf_clearance'};
            $cookie="Cookie: __cfduid=".$cfduid."; cf_clearance=".$cf_clearance."\r\n";
        }
    }
    /*$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_USERAGENT, $user_agent);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_URL, $url);
	
	curl_setopt ($ch, CURLOPT_COOKIE, $cookie);
	@curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
	$result = curl_exec($ch);
	$statusCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	curl_close($ch);*/
    
    $options = array(
        'http'=>array(
        'method'=>"GET",
        'header'=>"Accept-language: en\r\n" .
              $cookie .  // check function.stream-context-create on php.net
              "User-Agent: ".$user_agent."\r\n" // i.e. An iPad 
        )
    );

    $context = stream_context_create($options);
    $result=@file_get_contents($url, false, $context);
	return $result;
}

function isJson($string) {
 json_decode($string);
 return (json_last_error() == JSON_ERROR_NONE);
}

?>