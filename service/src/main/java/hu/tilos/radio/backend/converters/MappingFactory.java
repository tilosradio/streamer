package hu.tilos.radio.backend.converters;

import hu.radio.tilos.model.*;
import hu.tilos.radio.backend.data.CommentData;
import hu.tilos.radio.backend.data.UserLink;
import hu.tilos.radio.backend.data.types.*;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to create mappers.
 */
@Named
public class MappingFactory {


    public static SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    public static SimpleDateFormat HHMMSS = new SimpleDateFormat("HHmmss");

    @Inject
    TagUtil tagUtil;

    @Inject
    HTMLSanitizer sanitizer;

    @Inject
    StrictHTMLSanitizer strictSanitizer;

    @Inject
    EntityManager manager;

    @Inject
    EntityChildMapper entityChildMapper;

    private String uploadUrl = "http://tilos.hu/upload/";

    public static DozerBeanMapper createDozer(EntityManager em) {
        DozerBeanMapper mapper = new DozerBeanMapper();
        Map<String, CustomConverter> customConvertersWithId = new HashMap<>();
        customConvertersWithId.put(ChildEntityFieldConverter.ID, new ChildEntityFieldConverter(em));
        mapper.setCustomConvertersWithId(customConvertersWithId);
        return mapper;
    }


    public static DozerBeanMapper createDozer(EntityManager em, BeanMappingBuilder builder) {
        DozerBeanMapper mapper = createDozer(em);
        mapper.addMapping(builder);
        return mapper;
    }

    @Produces
    @Default
    public ModelMapper createModelMapper() {
        final Converter<String, String> uploadUrlConverter = new PrefixingConverter(uploadUrl);
        final Converter<String, String> sounds = new PrefixingConverter("http://archive.tilos.hu/sounds/", "http");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Author, AuthorSimple>() {
            @Override
            protected void configure() {
                using(uploadUrlConverter).map().setAvatar(source.getAvatar());
            }
        });

        modelMapper.addMappings(new PropertyMap<TextContent, TextData>() {
            @Override
            protected void configure() {
                using(new Converter<String, String>() {

                    @Override
                    public String convert(MappingContext<String, String> context) {
                        return tagUtil.replaceToHtml(sanitizer.clean(context.getSource()));
                    }
                }).map().setFormatted(source.getContent());
            }
        });

        modelMapper.addMappings(new PropertyMap<Mix, MixSimple>() {
            @Override
            protected void configure() {
                using(sounds).map().setLink(source.getFile());
                using(new AbstractConverter<String, Boolean>() {
                    @Override
                    protected Boolean convert(String source) {
                        if (source == null) {
                            return false;
                        }
                        return source.length() > 10;
                    }
                }).map(source.getContent()).setWithContent(false);
            }
        });
        modelMapper.addMappings(new PropertyMap<Mix, MixData>() {
            @Override
            protected void configure() {
                using(sounds).map().setLink(source.getFile());
            }
        });
        modelMapper.addMappings(new PropertyMap<User, UserLink>() {
            @Override
            protected void configure() {

            }
        });
        modelMapper.addMappings(new PropertyMap<MixData, Mix>() {
            @Override
            protected void configure() {
                using(new AbstractConverter<String, Date>() {

                    @Override
                    protected Date convert(String source) {
                        if (source == null) {
                            return null;
                        }
                        try {
                            return new SimpleDateFormat("yyyy-MM-dd").parse(source);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).map(source.getDate()).setDate(null);
                using(entityChildMapper).map(source.getShow()).setShow(null);
            }
        });
        modelMapper.addMappings(new PropertyMap<EpisodeData, Episode>() {
            @Override
            protected void configure() {
                using(entityChildMapper).map(source.getShow()).setShow(null);
            }
        });
        modelMapper.addMappings(new PropertyMap<Episode, EpisodeData>() {
            @Override
            protected void configure() {
                using(new Converter<Episode, String>() {
                    @Override
                    public String convert(MappingContext<Episode, String> context) {
                        Episode episode = context.getSource();
                        if (episode.getRealTo().compareTo(new Date()) < 0) {
                            return "http://tilos.hu/mp3/tilos-" +
                                    YYYYMMDD.format(episode.getRealFrom()) +
                                    "-" +
                                    HHMMSS.format(episode.getRealFrom()) +
                                    "-" +
                                    HHMMSS.format(episode.getRealTo()) + ".m3u";
                        } else {
                            return null;
                        }
                    }
                }).map(source).setM3uUrl(null);
            }
        });
        modelMapper.addMappings(new PropertyMap<Comment, CommentData>() {
            @Override
            protected void configure() {
                using(new Converter<String, String>() {

                    @Override
                    public String convert(MappingContext<String, String> context) {
                        return strictSanitizer.clean(context.getSource().replaceAll("\n", "<br/>"));
                    }
                }).map(source.getComment()).setComment(null);
            }
        });
        return modelMapper;

    }
}
