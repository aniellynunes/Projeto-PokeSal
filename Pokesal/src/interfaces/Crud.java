package interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Interface generica para funções de CRUD.
 *
 * @param <T>   entidade armazenada.
 * @param <ID>  identificador único.
 */
public interface Crud<T, ID> {
    /** Salva registro no repositorio. */
    void salvar(T entidade) throws Exception;
    /** Busca registro por id. */
    Optional<T> buscarPorId(ID id);
    /** Retorna todos os registros armazenados. */
    List<T> listarTodos();
}
