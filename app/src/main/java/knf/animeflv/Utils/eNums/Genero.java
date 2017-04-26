package knf.animeflv.Utils.eNums;


public enum Genero {
    TODOS(0),
    Accion(1),
    ArtesMarciales(2),
    Aventuras(3),
    Carreras(4),
    Comedia(5),
    Demensia(6),
    Demonios(7),
    Deportes(8),
    Drama(9),
    Ecchi(10),
    Escolares(11),
    Espacial(12),
    Fantasia(13),
    CienciaFiccion(14),
    Harem(15),
    Historico(16),
    Infantil(17),
    Josei(18),
    Juegos(19),
    Magia(20),
    Mecha(21),
    Militar(22),
    Misterio(23),
    Musica(24),
    Parodia(25),
    Policia(26),
    Psicologico(27),
    Recuentosdelavida(28),
    Romance(29),
    Samurai(30),
    Seinen(31),
    Shoujo(32),
    Shounen(33),
    SinGenero(34),
    Sobrenatural(35),
    Superpoderes(36),
    Suspenso(37),
    Terror(38),
    Vampiros(39),
    Yaoi(40),
    Yuri(41);
    int value;

    Genero(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
