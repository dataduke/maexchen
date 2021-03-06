from udp_kommunikator import UdpKommunikator


def sende_und_empfange_echo(message):
    maexchen.sende_nachricht("ECHO", [message])
    (kommando, paramter) = maexchen.warte_auf_nachricht()

    print("Kommando erhalten:", kommando)
    print("   mit Parametern:", paramter)


udp_ip = "127.0.0.1"
udp_port = 9000

maexchen = UdpKommunikator(server_ip=udp_ip, server_port=udp_port)

for index in range(10):
    sende_und_empfange_echo("Hallo: " + str(index))
