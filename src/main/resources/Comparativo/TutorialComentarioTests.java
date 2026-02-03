package org.iesvdm.tutorial;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.tutorial.domain.Comentario;
import org.iesvdm.tutorial.domain.Tutorial;
import org.iesvdm.tutorial.repository.ComentarioRepository;
import org.iesvdm.tutorial.repository.TutorialRepository;
import org.iesvdm.tutorial.util.UtilJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;

/**
 * TEST ONETOMANY ORPHANREMOVAL, CASCADE.ALL, LAZY
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class )
@SpringBootTest
public class TutorialComentarioTests {

    @Autowired
    TutorialRepository tutorialRepository;
    @Autowired
    ComentarioRepository comentarioRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;
    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Order(0)
    void pruebaFetchLazyEager() {
        //¿Por qué? la coleccion comentarios @OneToMany es un fetch LAZY, sin transaccion no lanza
        //La query para cargar

        //

        //Iniciamos una sesión para que no nos falle al ser Fetch LAZY
        //Transacción para que no de fallo
        transactionTemplate.execute(status -> {

            Tutorial tutorial = tutorialRepository.save(Tutorial.builder()
                    .titulo("Título 1")
                    .build());

            Tutorial tutorial2 = tutorialRepository.findById(tutorial.getId()).orElse(null);

            System.out.println(tutorial2);

            return null;

        });


    }

    @Test
    @Order(1)
    public void grabarPadreAHijosPorColeccion() {

        Tutorial tutorial = new Tutorial(0,"Título 1", new HashSet<>());

        Comentario comentario1 = new Comentario(0, "Texto1",tutorial);
        tutorial.getComentarios().add(comentario1);
        //NO PUEDO AÑADIR MÁS COMENTARIOS SIN SALVAR ANTES TUTORIAL
        //DADO QUE LA COLECCIÓN ES UN SET Y NO PUEDO TENER 2 COMENTARIOS
        // CON ID = 0 EN LA COLECCIÓN SET DE ARRANQUE, SI CAMBIO A
        //COLECCIÓN ARRAYLIST SÍ PODRÍA

        tutorialRepository.save(tutorial);

        Comentario comentario2 = new Comentario(0, "Texto2",tutorial);
        tutorial.getComentarios().add(comentario2);

        tutorialRepository.save(tutorial);

    }
    @Test
    @Order(2)
    public void grabarPadreAHijosSinColeccion() {

        Tutorial tutorial = new Tutorial(0,"Título 2", new HashSet<>());
        tutorialRepository.save(tutorial);

        Comentario comentario3 = new Comentario(0, "Texto3",tutorial);
        tutorial.getComentarios().add(comentario3);
        comentarioRepository.save(comentario3);

        Comentario comentario4 = new Comentario(0, "Texto4", tutorial);
        tutorial.getComentarios().add(comentario4);
        comentarioRepository.save(comentario4);
        //Por seguridad mejor hacerlo pero no hace nada
        //tutorialRepository.save(tutorial);

    }

    @Test
    @Order(3)
    public void actualizarHijoDePadre() {

        Tutorial tutorial = tutorialRepository.findById(4L).orElse(null);

        //Si se utliza un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Comentario> comentarios = entityManager.createQuery(
//                        "select c " +
//                                "from Comentario c " +
//                                "join fetch c.tutorial " +
//                                "where c.tutorial.id = :id", Comentario.class)
//                .setParameter("id", tutorial.getId())
//                .getResultList();
//        tutorial.setComentarios(new HashSet<>(comentarios));
        UtilJPA.initializeLazyOneToManyByJoinFetch(entityManager,
                Tutorial.class,
                Comentario.class,
                tutorial.getId(),
                tutorial::setComentarios
        );
        //

        tutorial.getComentarios().forEach(System.out::println);

        Comentario comentarioAActualizar = tutorial.getComentarios().stream().findFirst().orElse(null);
        System.out.println("Comentario a ACTUALIZAR: " + comentarioAActualizar);

        comentarioAActualizar.setTexto("EH Comentario Tutorial 1 Actualizado!!!!!!");

        tutorialRepository.save(tutorial);

    }

    @Test
    @Order(4)
    public void actualizarPorHijo() {

        Tutorial tutorial = tutorialRepository.findById(3L).orElse(null);

        //Si se utliza un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Comentario> comentarios = entityManager.createQuery(
//                        "select c " +
//                                "from Comentario c " +
//                                "join fetch c.tutorial " +
//                                "where c.tutorial.id = :id", Comentario.class)
//                .setParameter("id", tutorial.getId())
//                .getResultList();
//        tutorial.setComentarios(new HashSet<>(comentarios));
        //
        UtilJPA.initializeLazyOneToManyByJoinFetch(entityManager,
                Tutorial.class,
                Comentario.class,
                tutorial.getId(),
                tutorial::setComentarios
        );

        tutorial.getComentarios().forEach(System.out::println);

        Comentario comentarioAActualizar = tutorial.getComentarios().stream().findFirst().orElse(null);
        System.out.println("Comentario a ACTUALIZAR: " + comentarioAActualizar);

        comentarioAActualizar.setTexto("EH Comentario Tutorial 2 Actualizado!!!!!!");

        //Esta operacion no necesita Cascade.ALL
        comentarioRepository.save(comentarioAActualizar);


    }
    @Test
    @Order(5)
    public void borrarHijoDePadre() {

        Tutorial tutorial = tutorialRepository.findById(5L).orElse(null);

        //Si se utlizas un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Comentario> comentarios = entityManager.createQuery(
//                        "select c " +
//                                "from Comentario c " +
//                                "join fetch c.tutorial " +
//                                "where c.tutorial.id = :id", Comentario.class)
//                .setParameter("id", tutorial.getId())
//                .getResultList();
//        tutorial.setComentarios(new HashSet<>(comentarios));
        UtilJPA.initializeLazyOneToManyByJoinFetch(entityManager,
                Tutorial.class,
                Comentario.class,
                tutorial.getId(),
                tutorial::setComentarios
        );
        //

        tutorial.getComentarios().forEach(System.out::println);

        Comentario comentarioABorrar = tutorial.getComentarios().stream().findFirst().orElse(null);
        System.out.println("Comentario a BORRAR: " + comentarioABorrar);

        //TECNICA BORRADO POR ENTIDAD HUERFANA
        //No aplicar la logica es un sistema de JPA
        //FLAG -> orphanRemoval = true, cascade = CascadeType.ALL
        comentarioABorrar.setTutorial(null);
        tutorial.getComentarios().remove(comentarioABorrar);
        tutorialRepository.save(tutorial);
        //
    }

    @Test
    @Order(6)
    public void borrarPorHijo() {

        Tutorial tutorial = tutorialRepository.findById(2L).orElse(null);

        //Si se utliza un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Comentario> comentarios = entityManager.createQuery(
//                        "select c " +
//                                "from Comentario c " +
//                                "join fetch c.tutorial " +
//                                "where c.tutorial.id = :id", Comentario.class)
//                .setParameter("id", tutorial.getId())
//                .getResultList();
//        tutorial.setComentarios(new HashSet<>(comentarios));
        UtilJPA.initializeLazyOneToManyByJoinFetch(entityManager,
                Tutorial.class,
                Comentario.class,
                tutorial.getId(),
                tutorial::setComentarios
        );
        //

        tutorial.getComentarios().forEach(System.out::println);

        Comentario comentarioABorrar = tutorial.getComentarios().stream().findFirst().orElse(null);
        System.out.println("Comentario a BORRAR: " + comentarioABorrar);

        comentarioRepository.delete(comentarioABorrar);

    }
    @Test
    @Order(7)
    public void borrarPorTodoPadre() {

        Tutorial tutorial = tutorialRepository.findById(1L).orElse(null);
        tutorialRepository.delete(tutorial);

    }

    @Test
    @Order(8) //Test Opcionalmente
    public void borrarTodosLosComentarios() {

        transactionTemplate.execute(status -> {

            Tutorial tutorial = tutorialRepository.findById(4L).orElse(null);

            tutorial.getComentarios().forEach(comentario -> {
                comentario.setTutorial(null);
                //Hay que hacerlo con un HashSet
                //Set<Comentario>

                comentarioRepository.delete(comentario);
            });


            //tutorial.getComentarios().forEach(comentario -> comentarioRepository.delete(comentario));
            return null;

        });


    }

}
