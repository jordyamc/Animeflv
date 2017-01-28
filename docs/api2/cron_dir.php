<?php
$url="http://animeflvapp.pythonanywhere.com/v1/content?type=html&url=http://animeflv.net/ajax/animes/lista_completa";
    $repPOS=str_replace("POS","://",$url);
    $source= @file_get_contents($url);
    $cache="0";
    if (strpos($source,"var lanime")===false){
        echo "Error";
    }
    else{
        if (file_exists("../cache/dir.txt")){
            $cont=file_get_contents("../cache/dir.txt");
            if ($source!==$cont){
                file_put_contents("../cache/dir.txt",$source);
                echo "Actualizado";
            }
            else{
                echo "Igual";
            }
            
        }
        else{
            file_put_contents("../cache/dir.txt",$source);
            echo "Crear Cache";
        }
    }
?>