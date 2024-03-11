package ch.unisg.ics.interactions.hmas.interaction.signifiers;

import ch.unisg.ics.interactions.hmas.core.hostables.BaseResourceProfile;
import ch.unisg.ics.interactions.hmas.core.hostables.ProfiledResource;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceProfile extends BaseResourceProfile {

  private final Set<Signifier> signifiers;

  protected ResourceProfile(AbstractBuilder builder) {
    super(builder);
    this.signifiers = ImmutableSet.copyOf(builder.signifiers);
  }

  public Set<Signifier> getExposedSignifiers() {
    return this.signifiers;
  }

  public Set<Signifier> getExposedSignifiers(String actionSemanticType) {

    return this.signifiers.stream()
            .filter(sig -> sig.getActionSpecification().getSemanticTypes().contains(actionSemanticType))
            .collect(Collectors.toSet());
  }

  public Optional<Signifier> getFirstExposedSignifier(String actionSemanticType) {
    return this.signifiers.stream()
            .filter(sig -> sig.getActionSpecification().getSemanticTypes().contains(actionSemanticType))
            .findFirst();
  }

  public static class Builder extends AbstractBuilder<Builder, ResourceProfile> {

    public Builder(ProfiledResource resource) {
      super(resource);
    }

    @Override
    protected Builder getBuilder() {
      return this;
    }

    public ResourceProfile build() {
      return new ResourceProfile(this);
    }
  }

  public abstract static class AbstractBuilder<S extends AbstractBuilder, T extends BaseResourceProfile>
          extends BaseResourceProfile.AbstractBuilder<S, T> {

    private final Set<Signifier> signifiers;

    public AbstractBuilder(ProfiledResource resource) {
      super(resource);
      this.signifiers = new HashSet<>();
    }

    public S exposeSignifier(Signifier signifier) {
      this.signifiers.add(signifier);
      return getBuilder();
    }

    public S exposeSignifiers(Set<Signifier> signifiers) {
      this.signifiers.addAll(signifiers);
      return getBuilder();
    }

    public abstract T build();
  }
}
