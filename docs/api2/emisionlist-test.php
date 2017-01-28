<?php
    if(isset($_GET['clean'])){
        $files = scandir('../times/list/');
        $contador=0;
        $aids=array();
        foreach($files as $file) {
            $data=json_decode(file_get_contents('../cache/aid/'.str_replace(".data",".txt",$file)));
            if (!is_null($data)){
                $ffin=$data->fecha_fin;
                if(!is_null($ffin)){
                    if ($ffin==="1"){
                        $j=new stdClass();
                        $j->name=$data->titulo;
                        $j->filename=$file;
                        array_push($aids,$j);
                        unlink('../times/list/'.$file);
                        $contador++;
                    }
                }
            }
        }
        $json=new stdClass();
        $json->version=file_get_contents("../ver.txt");
        $json->cleaned=$contador;
        $json->details=$aids;
        header('Content-Type: application/json');
        echo json_encode($json);
    }else{
        $json=new stdClass();
        $d1=array();
        $d2=array();
        $d3=array();
        $d4=array();
        $d5=array();
        $d6=array();
        $d7=array();
        $files = scandir('../times/list/');
        foreach($files as $file) {
            $data=json_decode(file_get_contents('../times/list/'.$file));
            if (!is_null($data)){
                switch ($data->daycode){
                    case "1":
                        array_push($d1,$data);
                        break;
                    case "2":
                        array_push($d2,$data);
                        break;
                    case "3":
                        array_push($d3,$data);
                        break;
                    case "4":
                        array_push($d4,$data);
                        break;
                    case "5":
                        array_push($d5,$data);
                        break;
                    case "6":
                        array_push($d6,$data);
                        break;
                    case "7":
                        array_push($d7,$data);
                        break;
                }

            }
        }
        $days=array();
        array_push($days,$d1);
        array_push($days,$d2);
        array_push($days,$d3);
        array_push($days,$d4);
        array_push($days,$d5);
        array_push($days,$d6);
        array_push($days,$d7);
        $json->version=file_get_contents("../ver.txt");
        $json->emision=$days;
        header('Content-Type: application/json');
        echo json_encode($json);
    }
?>