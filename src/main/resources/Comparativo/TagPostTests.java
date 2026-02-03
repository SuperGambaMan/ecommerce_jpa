package org.iesvdm.tutorial;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesvdm.tutorial.domain.Post;
import org.iesvdm.tutorial.domain.Tag;
import org.iesvdm.tutorial.repository.PostRepository;
import org.iesvdm.tutorial.repository.TagRepository;
import org.iesvdm.tutorial.util.UtilJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * MANYTOMANY CON CASCADE {PERSIST, MERGE} POR LADO PROPIETARIO (LADO COM MAPPEDBY)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class )
@SpringBootTest
public class TagPostTests {

    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;

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
    @Order(1)
    void grabarPorPropietarioDeManyToMany() {

        Tag tag1 = new Tag(null, "Programación", new HashSet<>());
        Tag tag2 = new Tag(null, "Base de datos", new HashSet<>());

        Post post1 = new Post(null, "Post1 - Programando fácil con JPA :P",new HashSet<>());

        post1.addTag(tag1);
        //RECUERDA QUE LA COLECCIÓN DE TAGS ES UN SET Y NO PUEDE HABER REPETIDOS CON EL
        //MISMO ID
        postRepository.save(post1);

        post1.addTag(tag2);
        postRepository.save(post1);

    }

    @Test
    @Order(2)
    void grabarTagQueYaExiste() {
        transactionTemplate.execute(status -> {
            //Lo creamos y lo salvamos para que no entre en conflicto con el SET y se lo cargue
            Tag tag3 = new Tag(null, "EEEH Tag 3!!!!", new HashSet<>());
            tagRepository.save(tag3);

            Post post2 = new Post(null, "Post2 - NO programando tan fácilmente...", new HashSet<>());
            postRepository.save(post2);

        //Si se utlizas un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Tag> tags = entityManager.createQuery(
//                        "select t " +
//                                "from Tag t " +
//                                "join fetch t.posts ps " +
//                                "where ps.id = :id", Tag.class)
//                .setParameter("id", post2.getId())
//                .getResultList();
//        post2.setTags(new HashSet<>(tags));

//        UtilJPA.initializeLazyManyToManyByJoinFetch(entityManager,
//                Tag.class,
//                Post.class,
//                post2.getId(),
//                post2::setTags
//        );

            Tag tag1 = tagRepository.findById(1L).orElse(null);
        //Si se utlizas un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Post> posts = entityManager.createQuery(
//                        "select p " +
//                                "from Post p " +
//                                "join fetch p.tags ts " +
//                                "where ts.id = :id", Post.class)
//                .setParameter("id", tag.getId())
//                .getResultList();
//        tag.setPosts(new HashSet<>(posts));

//        UtilJPA.initializeLazyManyToManyByJoinFetch(entityManager,
//                Post.class,
//                Tag.class,
//                tag1.getId(),
//                tag1::setPosts
//        );

            post2.addTag(tag1);
        //addTag(Tag tag){
        // tags.add(tag);
        //tag.getPosts().add(this);
        // }
            post2.addTag(tag3);
            postRepository.save(post2);
            return null;
        });

    }

    @Test
    @Order(3)
    void desasociarTag() {

        Tag tag1 = tagRepository.findById(1L).orElse(null);
        //Si se utliza un fetch LAZY, la mejor estrategia es realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Post> posts = entityManager.createQuery(
//                        "select p " +
//                                "from Post p " +
//                                "join fetch p.tags ts " +
//                                "where ts.id = :id", Post.class)
//                .setParameter("id", tag.getId())
//                .getResultList();
//        tag.setPosts(new HashSet<>(posts));
        UtilJPA.initializeLazyManyToManyByJoinFetch(entityManager,
                Post.class,
                Tag.class,
                tag1.getId(),
                tag1::setPosts
        );
        //

        ArrayList<Post> auxCopyPosts = new ArrayList<>(tag1.getPosts());
        auxCopyPosts.forEach(post -> {
            //post.getTags().remove(tag1);
            tag1.getPosts().remove(post);
        });
        //SE PUEDE DESVINCULAR EL TAG DEL MANYTOMANY DADO QUE
        //NO ES EL PROPIETARIO, EN ESTE CASO EL PROPIETARIO
        //DE LA RELACION ES POST
        tagRepository.save(tag1);
    }

    @Test
    @Order(4)
    void borrarTag() {

        Tag tag2 = tagRepository.findById(2L).orElse(null);
        //Si se utlizas un fetch LAZY, mejor estrategia realizar un join fetch en JPQL
        //y cargar en la colección. NOTA: si utilizas EAGER puedes prescindir de join fetch.
//        List<Post> posts = entityManager.createQuery(
//                        "select p " +
//                                "from Post p " +
//                                "join fetch p.tags ts " +
//                                "where ts.id = :id", Post.class)
//                .setParameter("id", tag.getId())
//                .getResultList();
//        tag.setPosts(new HashSet<>(posts));
        UtilJPA.initializeLazyManyToManyByJoinFetch(entityManager,
                Post.class,
                Tag.class,
                tag2.getId(),
                tag2::setPosts
        );
        //

        ArrayList<Post> auxCopyPosts = new ArrayList<>(tag2.getPosts());
        auxCopyPosts.forEach(post -> {
            post.getTags().remove(tag2);
            tag2.getPosts().remove(post);
        });
        //SE PUEDE DESVINCULAR EL TAG DEL MANYTOMANY DADO QUE
        //NO ES EL PROPIETARIO, EN ESTE CASO EL PROPIETARIO
        //DE LA RELACION ES POST
        tagRepository.delete(tag2);

    }

    @Test
    @Order(5)
    void grabarPostQueYaExiste() {
        transactionTemplate.execute(status -> {
            //Lo creamos y lo salvamos para que no entre en conflicto con el SET y se lo cargue
            Tag tag4 = new Tag(null, "EEEH Tag 4!!!!", new HashSet<>());
            tagRepository.save(tag4);

            Post post3 = new Post(null, "Post3 - NO programando tan fácilmente...", new HashSet<>());
            //postRepository.save(post3);

            Post post1 = postRepository.findById(1L).orElse(null);

//            addPost(Post post){
//                posts.add(post);
//                post.getTags().add(this);
//            }
            tag4.addPost(post3);

            tag4.addPost(post1);
            tagRepository.save(tag4);
            return null;
        });
    }
}
