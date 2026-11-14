package interfaces;

import enums.Campos;
import java.time.LocalTime;

/**
 * Fornece as condições climáticas e de terreno da arena com base no horário.
 */
public interface CampoService {
    /** Obtém o cenário atual considerando o horário fornecido. */
	Campos getField(LocalTime horaAtual);
}
