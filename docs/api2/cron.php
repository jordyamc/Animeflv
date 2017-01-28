<?php
$url="http://animeflvapp.pythonanywhere.com/v1/content?type=html&url=http://animeflv.net";
    $repPOS=str_replace("POS","://",$url);
    $source= @file_get_contents($url);
    $cache="0";
    if (strpos($source,"<title>Anime Online - AnimeFLV</title>")===false){
        echo "Error";
    }
    else{
        if (file_exists("../cache/inicio.txt")){
            $cont=file_get_contents("../cache/inicio.txt");
            if ($source!==$cont){
                file_put_contents("../cache/inicio.txt",$source);
                echo "Actualizado";
            }
            else{
                echo "Igual";
            }
            
        }
        else{
            file_put_contents("../cache/inicio.txt",$source);
            echo "Crear Cache";
        }
    }
?>