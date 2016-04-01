package knf.animeflv.Utils.eNums;


public enum Genero {
    TODOS(0),
    Accion(1),
    Aventuras(2),
    Carreras(3),
    Comedia(4),
    Cyberpunk(5),
    Deportes(6),
    Drama(7),
    Ecchi(8),
    Escolares(9),
    Fantasía(10),
    CienciaFiccion(11),
    Gore(12),
    Harem(13),
    Horror(14),
    Josei(15),
    Lucha(16),
    Magia(17),
    Mecha(18),
    Militar(19),
    Misterio(20),
    Musica(21),
    Parodias(22),
    Psicologico(23),
    Recuerdosdelavida(24),
    Romance(25),
    Seinen(26),
    Shojo(27),
    Shonen(28),
    SinGenero(29),
    Sobrenatural(30),
    Vampiros(31),
    Yaoi(32),
    Yuri(33);
    int value;

    Genero(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
