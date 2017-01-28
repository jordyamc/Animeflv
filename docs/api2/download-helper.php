<?php
ini_set('default_socket_timeout', 1);
if (isset($_GET['down_url'])){
    if (isset($_GET['json_data'])){
        $url=$_GET['down_url'];
        $json=json_decode(urldecode($_GET['json_data']));
        $datas=array();
        foreach ($json->data as $object){
            $datas=$datas+array($object->key=>$object->value);
        }
        $options = array(
            'http' => array(
                'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
                'method'  => 'POST',
                'content' => http_build_query($datas)
            )
        );
        $context  = stream_context_create($options);
        @file_get_contents($url,false,$context);
        foreach ($http_response_header as $string){
            if (strpos($string,"Location")!==false){
                header($string);
                //echo str_replace("Location: ","",$string);
                break;
            }
        }
    }else {
        header('Content-Type: application/json');
        header("HTTP/1.1 404 Not Found");
        $json = new stdClass();
        $json->response = "No Data";
        echo json_encode($json);
    }
}else{
    header('Content-Type: application/json');
    header("HTTP/1.1 404 Not Found");
    $json=new stdClass();
    $json->response="No URL";
    echo json_encode($json);
}