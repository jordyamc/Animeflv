<!DOCTYPE html>
<html lang="">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title></title>
</head>

<body>
    <?php
$filename = 'contador.txt';
if (isset($_GET["id"]) and isset($_GET["version"])){
    $ver=$_GET['version'];
    $file="../versiones/".$ver.".txt";
    $total="../versiones/total.txt";
    $id=$_GET['id'];
    if (file_exists($file)){
        $ids=file_get_contents($file);
        if (strpos($ids,$id) === false) {
            $Content=$id.':::';
            if(!file_put_contents($file, $Content, FILE_APPEND)){
                echo 'Error';
            }
            else{
                $idsT=file_get_contents($total);
                if (strpos($idsT,$id) === false) {
                    $Content=$id.':::';
                    if(!file_put_contents($total, $Content, FILE_APPEND)){
                        echo 'Error';
                    }
                    else{
                        echo 'ID Agregado '.$id; 
                    }
                }
            }
        }
        else{
            $idsT=file_get_contents($total);
            if (strpos($idsT,$id) === false) {
                $Content=$id.':::';
                if(!file_put_contents($total, $Content, FILE_APPEND)){
                    echo 'Error';
                }
                else{
                    echo 'ID Agregado '.$id; 
                }
            }
            else{
                echo 'ID '.$id.' ya existe';
            }
        }
    }
    else{
        $Content=$id.':::';
        if(!file_put_contents($file, $Content)){
            echo 'Error';
        }
        else{
            $idsT=file_get_contents($total);
            if (strpos($idsT,$id) === false) {
                $Content=$id.':::';
                if(!file_put_contents($total, $Content, FILE_APPEND)){
                    echo 'Error';
                }
                else{
                    echo 'ID Agregado '.$id." y version ".$ver." iniciada.";
                }
            }
        }
    }
}
else{
    if (isset($_GET['version'])){
        $ver=$_GET["version"];
        if ($ver != 'total'){
        $file="../versiones/".$ver.".txt";
        if (file_exists($file)){
            $ids=file_get_contents($file);
            $array=explode(":::",$ids);
            foreach($array as $Tid){
                echo $Tid."."."<br>";   
            }
        }
        else{
            echo "Version ".$ver." sin registros.";
        }
    }
    else{
        $file="../versiones/".$ver.".txt";
        if (file_exists($file)){
        $ids=file_get_contents($file);
        $array=explode(":::",$ids);
        echo count($array)-1;
    }
        else{
            echo "0";      
        }
    }
    }
    else{
        foreach(glob('../versiones/*.*') as $filename){
            $lista=$lista.$filename.':::';
        }
        $vers=explode(":::",$lista);
        foreach($vers as $ints){
            if (file_exists($ints)){
                $ver=substr($ints,strrpos($ints,"/")+1);
                $num=substr($ver,0,strpos($ver,"."));
                if($num != "total"){
                    if(intval($num)>=100){
                        $orgMA=$orgMA.$ints.':::';
                    }
                    else{
                        $orgM=$orgM.$ints.':::';
                    }
                }
                else{
                    $orgMA=$orgMA.$ints.':::';
                }
            }
        }
        $orgs=$orgM.$orgMA;
        $dirss=explode(":::",$orgs);
        foreach($dirss as $dir){
            if (file_exists($dir)){
                $ids=file_get_contents($dir);
                $array=explode(":::",$ids);
                $ver=substr($dir,strrpos($dir,"/")+1);
                $ver1=substr($ver,0,strpos($ver,"."));
                $cuenta=count($array)-1;
                if ($cuenta<10){
                    $Scuenta=": ----[ ".$cuenta."   ]----";
                }
                else{
                    if ($cuenta>=10 and $cuenta<100){
                        $Scuenta=": ----[ ".$cuenta."  ]----";
                    }
                    else{
                        if ($cuenta>=100){
                            $Scuenta=": ----[ ".$cuenta." ]----";
                        }
                    }
                }
                if ($ver1 != "total"){
                    $echos=$echos."Version ".$ver1.$Scuenta."<br>";
                }
            }
        }
        $total="../versiones/total.txt";
        if (file_exists($total)){
            $idsT=file_get_contents($total);
            $arrayT=explode(":::",$idsT);
            $cuentaT=count($arrayT)-1;
            $echos=$echos."Total: ----[ ".$cuentaT." ]----";
        }
        echo $echos;
    }
}  
?>
</body>

</html>